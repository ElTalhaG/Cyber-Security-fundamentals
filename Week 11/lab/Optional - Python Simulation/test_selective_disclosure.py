import unittest
from datetime import date

from selective_disclosure import (
    CredentialEvidence,
    age_over_threshold_presentation,
    create_credential,
    selective_disclosure,
    verify_age_presentation,
    verify_student_presentation,
)


TODAY = date(2026, 11, 25)
TRUSTED = {"DTU"}
VALID = CredentialEvidence("DTU", True, date(2027, 8, 31))
EXPIRED = CredentialEvidence("DTU", True, date(2026, 11, 24))


class SelectiveDisclosureTests(unittest.TestCase):
    def setUp(self):
        self.credential = create_credential({
            "full_name": "Alice Johnson",
            "student_id": "20241234",
            "date_of_birth": "2002-05-14",
            "email": "alice@dtu.dk",
            "university": "DTU",
            "student_status": "active",
        })

    def test_student_disclosure_contains_only_requested_field(self):
        presentation = selective_disclosure(self.credential, ["student_status"])
        self.assertEqual(presentation, {"student_status": "active"})

    def test_student_active_is_accepted(self):
        result = verify_student_presentation(
            {"student_status": "active"}, VALID, trusted_issuers=TRUSTED, as_of=TODAY
        )
        self.assertTrue(result.accepted)

    def test_inactive_student_is_rejected(self):
        result = verify_student_presentation(
            {"student_status": "inactive"}, VALID, trusted_issuers=TRUSTED, as_of=TODAY
        )
        self.assertFalse(result.accepted)

    def test_missing_student_status_is_rejected(self):
        result = verify_student_presentation(
            {"university": "DTU"}, VALID, trusted_issuers=TRUSTED, as_of=TODAY
        )
        self.assertFalse(result.accepted)

    def test_expired_evidence_is_rejected(self):
        result = verify_student_presentation(
            {"student_status": "active"}, EXPIRED,
            trusted_issuers=TRUSTED, as_of=TODAY
        )
        self.assertFalse(result.accepted)
        self.assertIn("expired", result.reason)

    def test_untrusted_or_unauthenticated_evidence_is_rejected(self):
        untrusted = CredentialEvidence("Unknown", True, date(2027, 1, 1))
        unauthenticated = CredentialEvidence("DTU", False, date(2027, 1, 1))
        for evidence in (untrusted, unauthenticated):
            with self.subTest(evidence=evidence):
                result = verify_student_presentation(
                    {"student_status": "active"}, evidence,
                    trusted_issuers=TRUSTED, as_of=TODAY
                )
                self.assertFalse(result.accepted)

    def test_revoked_evidence_is_rejected(self):
        revoked = CredentialEvidence("DTU", True, date(2027, 1, 1), revoked=True)
        result = verify_student_presentation(
            {"student_status": "active"}, revoked,
            trusted_issuers=TRUSTED, as_of=TODAY
        )
        self.assertFalse(result.accepted)

    def test_selective_disclosure_rejects_unknown_field(self):
        with self.assertRaises(ValueError):
            selective_disclosure(self.credential, ["passport_number"])

    def test_age_presentation_only_discloses_predicate(self):
        presentation = age_over_threshold_presentation(self.credential, as_of=TODAY)
        self.assertEqual(presentation, {"age_over_18": True})

    def test_age_boundary_uses_calendar_date(self):
        credential = {"date_of_birth": "2008-11-26"}
        self.assertEqual(
            age_over_threshold_presentation(credential, as_of=TODAY),
            {"age_over_18": False},
        )

    def test_age_claim_must_be_boolean_and_true(self):
        for presentation in ({}, {"age_over_18": "true"}, {"age_over_18": False}):
            with self.subTest(presentation=presentation):
                result = verify_age_presentation(
                    presentation, VALID, trusted_issuers=TRUSTED, as_of=TODAY
                )
                self.assertFalse(result.accepted)


if __name__ == "__main__":
    unittest.main()
