# Question 11 - Onion Routing

## Task

Analyze which onion-routing statements hold for N1 → N3 → N4 → N5 → N10 when traffic analysis is excluded.

## Answer: B / C: ambiguity must be retained

There is no reliably unique answer from the wording alone.

**B is defensible:** N3 is the entry relay and receives N1's traffic directly. It knows that its immediate client/source is N1. It need not learn the plaintext M or the final destination N10 to know this local source relationship.

**C describes the intended idealized anonymity guarantee:** if an honest intermediate relay hides the input/output correspondence and the question truly rules out correlation, colluding relays cannot reconstruct the complete sender-to-recipient association across that honest hop. This requires the assumed mixing/cover-traffic model to hide that correspondence; layered encryption alone is not a blanket guarantee against every active or endpoint attack.

The subtle distinction is whether “can see that M originates from N1” in B means seeing the source of the incoming encrypted traffic, or recognizing a particular plaintext M and connecting it to a final recipient. The entry learns the former, but normally not the latter.

**Practice conclusion:** annotate B/C rather than inventing an official single-letter marking key. If the lecturer intends a statement about end-to-end unlinkability under a perfect-mixing abstraction, C is the likely conceptual target; B remains a valid observation about the first hop. Ask for the official interpretation before treating either as the sole grading answer.

## Assessing the alternatives

- A: the destination normally sees the last relay, not the original source, absent identifying content or another source of information.
- B: true for immediate-source visibility; not a claim that N3 decrypts M or identifies its destination.
- C: plausible under the stipulated idealized mixing/no-correlation assumptions, with the scope above.
- D: control of any single relay does not by itself reveal the complete route.

## Remember

Entry knows the client; exit knows the destination; this differs from linking both ends of a particular communication.

## Sources and scope

Original 2025 exam, PDF page 6. This is a reasoned practice solution, not an official marking key.

[Original Tor design, §1](https://svn-archive.torproject.org/svn/projects/design-paper/tor-design.html) describes predecessor/successor visibility. Real Tor traffic-analysis limitations must not be imported unchanged into an exam that explicitly excludes traffic analysis. No official key or lecture-specific privacy model was supplied.
