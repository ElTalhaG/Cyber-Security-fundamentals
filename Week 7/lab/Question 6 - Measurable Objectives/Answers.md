# Question 6 - Measurable Objectives

## Task

A company deploys an intrusion detection system but cannot judge its effect because it defined no benchmarks or KPIs. Identify the missing security-planning principle.

## Answer: B

The missing principle is **defining measurable objectives to assess effectiveness**. Deploying an IDS demonstrates that a tool was installed. Evaluating its contribution requires a defined goal, a baseline or reference workload, and a way to measure the result.

Useful measures should relate to the intended outcome and be interpreted together. Illustrative examples are:

| Measure | What it can show | Important qualification |
|---|---|---|
| Coverage of required network segments | Whether the intended traffic sources reach the IDS | Coverage alone does not prove detection quality |
| Detection of a defined set of authorized simulation scenarios | Whether relevant detection rules work against a repeatable reference | Success on known scenarios does not establish detection of every attack |
| Time from a scenario starting to its alert reaching an analyst | Detection and delivery delay | Response after delivery is a separate part of the process |
| Fraction of reviewed alerts that are false positives | Whether alert quality creates excessive investigation work | This depends on consistent review and labeling |

These are examples for explaining the answer, not measurements performed in this lab. Targets and a review period should be chosen before evaluation. Merely counting alerts is insufficient: more alerts may mean improved visibility, more attacks, or more noise.

## Why the other answers do not fit

| Option | Explanation |
|---|---|
| A | Evolution procedures help keep the system current. They do not directly supply the missing criteria for judging the IDS's effect. |
| C | Automated alerts are an implementation mechanism. Their existence does not establish whether the deployment meets a measurable objective. |
| D | Staff training matters, but the scenario specifically identifies absent benchmarks and KPIs. |

## Remember

**Define the intended outcome, the measurement method, and the baseline before claiming that a control improved security.**

## Source

Question sheet: PDF page 2, sixth question. Lecture 7: `02339-7.1.pdf`, PDF page 2 on measurable planning objectives, page 4 on establishing a baseline, and page 7 on verifiable requirements.
