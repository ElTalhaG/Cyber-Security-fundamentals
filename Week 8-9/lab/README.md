# Week 8-9 Protocol Security Lab

The supplied handout covers Week 8 protocol repair/implementation exercises and Week 9 follow-ups on NSL message confusion and password guessing. They are organized below by week.

## Exercise 1 - Init Protocol

- [Worked answer and OFMC instructions](Exercise%201%20-%20Init%20Protocol/Answers.md)
- [Repaired AnB model](Exercise%201%20-%20Init%20Protocol/init-fixed.AnB)
- Original instructor model: [`init.AnB`](init.AnB)

From this folder, check the repaired model on macOS with:

```bash
./ofmc-tool/ofmc2024.2/executables/mac/ofmc --numSess 2 "Exercise 1 - Init Protocol/init-fixed.AnB"
```

The other starter models are [`nspk.AnB`](nspk.AnB), [`nsl.AnB`](nsl.AnB), and [`password.AnB`](password.AnB). The handout labels the password and better-NSL discussion as Week 9.

## Exercise 2 - Implementing NSL

- [Implementation guide and run instructions](Exercise%202%20-%20Implement%20NSL/README.md)
- [Runnable Alice and Bob roles](Exercise%202%20-%20Implement%20NSL/src/main/java/lab/protocol/nsl/)

Run `./run.sh` in that folder to generate RSA keys, pass each encrypted message to the other role, and confirm the nonce checks succeed.

## Week 9

- [Better Implementing NSL: worked answer](Week%209/Exercise%201%20-%20Better%20Implementing%20NSL/Answers.md)
- [Password: worked answer and OFMC model](Week%209/Exercise%202%20-%20Password/Answers.md)
- [Password: repaired AnB model](Week%209/Exercise%202%20-%20Password/password-fixed.AnB)

From this folder, check the original password model with:

~~~bash
./ofmc-tool/ofmc2024.2/executables/mac/ofmc --numSess 2 password.AnB
~~~

Check the repaired model with:

~~~bash
./ofmc-tool/ofmc2024.2/executables/mac/ofmc --numSess 2 "Week 9/Exercise 2 - Password/password-fixed.AnB"
~~~

OFMC's no-attack result is bounded by the requested session count and the symbolic model.

## Tool

The included OFMC 2024 bundle is under [`ofmc-tool/ofmc2024.2/`](ofmc-tool/ofmc2024.2/). It contains platform executables, documentation, and examples; the handout's checks are run from a terminal.
