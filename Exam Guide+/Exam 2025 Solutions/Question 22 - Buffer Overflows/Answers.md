# Question 22 - Buffer Overflows

## Task

Identify the incorrect statement about buffer overflows.

## Answer: C

C is false because buffer overflows are not all obvious errors that are easy to find. Bugs may arise across multiple functions, size calculations, encodings, libraries, or unusual input paths, and even experienced programmers can miss them.

C normally leaves the programmer responsible for respecting object boundaries; ordinary pointer and array operations do not automatically enforce all bounds. A write beyond an allocated buffer can corrupt neighboring data or control state. An out-of-bounds read is also a memory-safety error, but should not be conflated with every write-overflow exploit.

## Assessing the alternatives

- A correctly points to the role of C's memory model and lack of automatic bounds enforcement.
- B correctly identifies missing or incorrect boundary checks as a cause.
- D correctly acknowledges that complex cases can evade experienced reviewers.

## Remember

Again, the prompt asks for the **incorrect** statement. Memory-safety bugs can be subtle even when their consequences are severe.

## Sources and scope

Original 2025 exam, PDF page 11. This is a reasoned practice solution, not an official marking key.
