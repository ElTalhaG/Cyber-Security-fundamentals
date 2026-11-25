"""Teaching simulation of selective disclosure; no real credential crypto."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import date
import json
from typing import Any, Mapping, Sequence


@dataclass(frozen=True)
class CredentialEvidence:
    """Results assumed to come from a separate credential validator."""

    issuer: str
    authenticated: bool
    expires_on: date
    revoked: bool = False


@dataclass(frozen=True)
class Decision:
    accepted: bool
    reason: str


def create_credential(attributes: Mapping[str, Any]) -> dict[str, Any]:
    """Make the exercise's simplified credential attributes."""
    return dict(attributes)


def selective_disclosure(
    credential: Mapping[str, Any], requested_attributes: Sequence[str]
) -> dict[str, Any]:
    """Return exactly the requested attributes, rejecting unknown requests."""
    missing = [name for name in requested_attributes if name not in credential]
    if missing:
        raise ValueError(f"Credential is missing requested attributes: {', '.join(missing)}")
    return {name: credential[name] for name in requested_attributes}


def age_over_threshold_presentation(
    credential: Mapping[str, Any], *, as_of: date, threshold: int = 18
) -> dict[str, bool]:
    """Calculate an age predicate without returning the birth date."""
    raw_birth_date = credential.get("date_of_birth")
    if not isinstance(raw_birth_date, str):
        raise ValueError("Credential is missing a valid date_of_birth")
    try:
        birth_date = date.fromisoformat(raw_birth_date)
    except ValueError as exc:
        raise ValueError("date_of_birth must use YYYY-MM-DD") from exc
    if birth_date > as_of:
        raise ValueError("date_of_birth cannot be in the future")

    age = as_of.year - birth_date.year
    if (as_of.month, as_of.day) < (birth_date.month, birth_date.day):
        age -= 1
    return {f"age_over_{threshold}": age >= threshold}


def _check_credential_evidence(
    evidence: CredentialEvidence, *, trusted_issuers: set[str], as_of: date
) -> Decision:
    if not evidence.authenticated:
        return Decision(False, "Credential authenticity was not established")
    if evidence.issuer not in trusted_issuers:
        return Decision(False, "Credential issuer is not trusted")
    if evidence.expires_on < as_of:
        return Decision(False, "Credential has expired")
    if evidence.revoked:
        return Decision(False, "Credential has been revoked")
    return Decision(True, "Credential evidence is acceptable")


def verify_student_presentation(
    presentation: Mapping[str, Any],
    evidence: CredentialEvidence,
    *,
    trusted_issuers: set[str],
    as_of: date,
) -> Decision:
    trust = _check_credential_evidence(evidence, trusted_issuers=trusted_issuers, as_of=as_of)
    if not trust.accepted:
        return trust
    if "student_status" not in presentation:
        return Decision(False, "Required student_status attribute is missing")
    if not isinstance(presentation["student_status"], str):
        return Decision(False, "student_status must be a string")
    if presentation["student_status"] != "active":
        return Decision(False, "student_status is not active")
    return Decision(True, "Active student status verified")


def verify_age_presentation(
    presentation: Mapping[str, Any],
    evidence: CredentialEvidence,
    *,
    trusted_issuers: set[str],
    as_of: date,
    threshold: int = 18,
) -> Decision:
    trust = _check_credential_evidence(evidence, trusted_issuers=trusted_issuers, as_of=as_of)
    if not trust.accepted:
        return trust
    claim_name = f"age_over_{threshold}"
    if claim_name not in presentation:
        return Decision(False, f"Required {claim_name} attribute is missing")
    claim = presentation[claim_name]
    if type(claim) is not bool:
        return Decision(False, f"{claim_name} must be a Boolean")
    if not claim:
        return Decision(False, f"Age requirement of {threshold} is not met")
    return Decision(True, f"Age requirement of {threshold} is met")


def demo() -> None:
    today = date(2026, 11, 25)
    credential = create_credential(
        {
            "full_name": "Alice Johnson",
            "student_id": "20241234",
            "date_of_birth": "2002-05-14",
            "email": "alice@dtu.dk",
            "university": "DTU",
            "student_status": "active",
        }
    )
    evidence = CredentialEvidence("DTU", True, date(2027, 8, 31))
    student = selective_disclosure(credential, ["student_status"])
    age = age_over_threshold_presentation(credential, as_of=today)

    print("Student presentation:")
    print(json.dumps(student, indent=2))
    print("Student decision:", verify_student_presentation(
        student, evidence, trusted_issuers={"DTU"}, as_of=today
    ))
    print("Age presentation:")
    print(json.dumps(age, indent=2))
    print("Age decision:", verify_age_presentation(
        age, evidence, trusted_issuers={"DTU"}, as_of=today
    ))
    print("Note: issuer authentication is simulated separately; this is not cryptographic proof.")


if __name__ == "__main__":
    demo()
