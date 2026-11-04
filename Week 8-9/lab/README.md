# Week 8-9 Protocol Security Lab

The supplied handout covers a formal protocol-repair exercise and an implementation exercise. The answer and model for the first exercise are available now; the Lowe-fixed Needham-Schroeder implementation is organized separately as Exercise 2.

## Exercise 1 - Init Protocol

- [Worked answer and OFMC instructions](Exercise%201%20-%20Init%20Protocol/Answers.md)
- [Repaired AnB model](Exercise%201%20-%20Init%20Protocol/init-fixed.AnB)
- Original instructor model: [`init.AnB`](init.AnB)

From this folder, check the repaired model on macOS with:

```bash
./ofmc-tool/ofmc2024.2/executables/mac/ofmc --numSess 2 "Exercise 1 - Init Protocol/init-fixed.AnB"
```

The other starter models are [`nspk.AnB`](nspk.AnB), [`nsl.AnB`](nsl.AnB), and [`password.AnB`](password.AnB). The handout labels the password and better-NSL discussion as Week 9.

## Tool

The included OFMC 2024 bundle is under [`ofmc-tool/ofmc2024.2/`](ofmc-tool/ofmc2024.2/). It contains platform executables, documentation, and examples; the handout's checks are run from a terminal.
