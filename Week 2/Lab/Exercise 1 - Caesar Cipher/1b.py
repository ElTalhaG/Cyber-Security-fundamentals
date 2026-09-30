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

ciphertext = """Lt wdas iwtht igjiwh id qt htau-tkxstci, iwpi paa btc pgt rgtpits tfjpa, iwpi
iwtn pgt tcsdlts qn iwtxg Rgtpidg lxiw rtgipxc jcpaxtcpqat Gxvwih, iwpi
pbdcv iwtht pgt Axut, Axqtgin pcs iwt ejghjxi du Wpeexcthh."""

key = 15

plaintext = caesar_decrypt(ciphertext, key)

print("Decrypted:", plaintext)