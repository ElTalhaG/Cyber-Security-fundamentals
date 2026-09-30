"""Columnar matrix transposition cipher from Week 2, Exercise 2."""


def transposition_encrypt(message: str, columns: int, padding: str = "x") -> str:
    if columns <= 0:
        raise ValueError("columns must be a positive integer")
    if len(padding) != 1:
        raise ValueError("padding must be one character")
    normalized = "".join(message.split())
    if not normalized:
        return ""
    normalized += padding * (-len(normalized) % columns)
    rows = len(normalized) // columns
    matrix = [normalized[i * columns:(i + 1) * columns] for i in range(rows)]
    return "".join(matrix[row][col] for col in range(columns) for row in range(rows))


def transposition_decrypt(ciphertext: str, columns: int, padding: str = "x") -> str:
    if columns <= 0:
        raise ValueError("columns must be a positive integer")
    if len(ciphertext) % columns:
        raise ValueError("ciphertext length must be divisible by columns")
    if not ciphertext:
        return ""
    rows = len(ciphertext) // columns
    matrix = [[""] * columns for _ in range(rows)]
    index = 0
    for col in range(columns):
        for row in range(rows):
        
            matrix[row][col] = ciphertext[index]
            index += 1
    plaintext = "".join("".join(row) for row in matrix)
    # Padding is inherently ambiguous; this removes only trailing pad characters.
    return plaintext.rstrip(padding)


if __name__ == "__main__":
    import sys
    if "--interactive" in sys.argv:
        message = input("Plaintext: ")
        columns = int(input("Number of columns: "))
    else:
        message = "HELLO WORLD"
        columns = 4
    ciphertext = transposition_encrypt(message, columns)
    recovered = transposition_decrypt(ciphertext, columns)
    print("Original (spaces removed):", "".join(message.split()))
    print("Encrypted:", ciphertext)
    print("Decrypted:", recovered)
