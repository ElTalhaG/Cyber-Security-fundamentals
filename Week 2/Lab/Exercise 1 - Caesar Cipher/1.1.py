def caesar_decrypt(ciphertext, key):
    result = ""

    for char in ciphertext:
        if char.isalpha():
            if char.isupper():
                base = ord('A')
            else:
                base = ord('a')

            shifted = (ord(char) - base - key) % 26
            result += chr(shifted + base)
        else:
            result += char

    return result


ciphertext = "KHOOR ZRUOG"

for key in range(26):
    plaintext = caesar_decrypt(ciphertext, key)
    print(f"Key {key}: {plaintext}")