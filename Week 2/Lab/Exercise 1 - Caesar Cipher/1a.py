def caesar_encrypt(message, key):
    result = ""

    for char in message:
        if char.isalpha():
            if char.isupper():
                base = ord('A')
            else:
                base = ord('a')

            shifted = (ord(char) - base + key) % 26
            result += chr(shifted + base)
        else:
            result += char

    return result


def caesar_decrypt(ciphertext, key):
    return caesar_encrypt(ciphertext, -key)

message = "If you can meet with triumph and disaster, and treat those two impostors just the same."
key = 7

ciphertext = caesar_encrypt(message, key)
plaintext = caesar_decrypt(ciphertext, key)

# print("Decrypted:", plaintext)
print("Encrypted:", ciphertext)