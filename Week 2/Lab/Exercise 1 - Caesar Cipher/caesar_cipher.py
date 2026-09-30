"""Caesar encryption, decryption, and brute-force helpers (Week 2, 1 and 1.1)."""


def caesar_encrypt(message: str, key: int) -> str:
    result = []
    for char in message:
        if "a" <= char <= "z":
            result.append(chr((ord(char) - ord("a") + key) % 26 + ord("a")))
        elif "A" <= char <= "Z":
            result.append(chr((ord(char) - ord("A") + key) % 26 + ord("A")))
        else:
            result.append(char)
    return "".join(result)


def caesar_decrypt(ciphertext: str, key: int) -> str:
    return caesar_encrypt(ciphertext, -key)


def brute_force(ciphertext: str) -> list[tuple[int, str]]:
    return [(key, caesar_decrypt(ciphertext, key)) for key in range(26)]


if __name__ == "__main__":
    import sys
    if "--interactive" in sys.argv:
        message = input("Message: ")
        key = int(input("Integer key: "))
        ciphertext = caesar_encrypt(message, key)
        print("Encrypted:", ciphertext)
        print("Decrypted:", caesar_decrypt(ciphertext, key))
    else:
        message = "If you can meet with triumph and disaster, and treat those two impostors just the same."
        key = 7
        ciphertext = caesar_encrypt(message, key)
        print("Example plaintext:", message)
        print("Key:", key)
        print("Encrypted:", ciphertext)
        print("Decrypted:", caesar_decrypt(ciphertext, key))
        print("\nBrute force for 'KHOOR ZRUOG':")
        for candidate_key, plaintext in brute_force("KHOOR ZRUOG"):
            print(f"Key {candidate_key:2}: {plaintext}")
