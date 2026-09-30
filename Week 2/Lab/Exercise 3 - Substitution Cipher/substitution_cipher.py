"""Monoalphabetic substitution cipher and simple frequency-analysis helpers."""

import random
import string

ALPHABET = string.ascii_uppercase


def validate_key(key: str) -> str:
    key = key.upper()
    if len(key) != 26 or set(key) != set(ALPHABET):
        raise ValueError("key must be a permutation of A-Z (each letter exactly once)")
    return key


def make_key() -> str:
    letters = list(ALPHABET)
    random.SystemRandom().shuffle(letters)
    return "".join(letters)


def encrypt(message: str, key: str) -> str:
    key = validate_key(key)
    mapping = str.maketrans(ALPHABET + ALPHABET.lower(), key + key.lower())
    return message.translate(mapping)


def decrypt(ciphertext: str, key: str) -> str:
    key = validate_key(key)
    mapping = str.maketrans(key + key.lower(), ALPHABET + ALPHABET.lower())
    return ciphertext.translate(mapping)


def letter_frequencies(text: str) -> list[tuple[str, int]]:
    counts = {letter: 0 for letter in ALPHABET}
    for char in text.upper():
        if char in counts:
            counts[char] += 1
    return sorted(counts.items(), key=lambda item: (-item[1], item[0]))


def ngram_frequencies(text: str, size: int = 2) -> list[tuple[str, int]]:
    if size < 1:
        raise ValueError("ngram size must be positive")
    letters = "".join(char for char in text.upper() if char in ALPHABET)
    counts = {}
    for index in range(len(letters) - size + 1):
        ngram = letters[index:index + size]
        counts[ngram] = counts.get(ngram, 0) + 1
    return sorted(counts.items(), key=lambda item: (-item[1], item[0]))


def word_pattern(word: str) -> str:
    """Return a pattern like PAPER -> 0.1.0.2.3, useful for word-shape clues."""
    labels = {}
    return ".".join(str(labels.setdefault(char, len(labels))) for char in word.upper())


def repeated_word_patterns(text: str) -> list[tuple[str, str]]:
    words = [word for word in text.upper().split() if word.isalpha()]
    seen = set()
    results = []
    for word in words:
        pattern = word_pattern(word)
        if pattern in seen:
            results.append((word, pattern))
        seen.add(pattern)
    return results


if __name__ == "__main__":
    key = make_key()
    message = ("Cryptography protects information by transforming readable text into a form "
               "that an unauthorized reader cannot understand. Repeated patterns can still "
               "reveal clues when a simple substitution cipher is used.")
    ciphertext = encrypt(message, key)
    print("Substitution key (cipher alphabet for A-Z):", key)
    print("Plaintext:", message)
    print("Ciphertext:", ciphertext)
    print("Recovered:", decrypt(ciphertext, key))
    print("Ciphertext letter frequencies:", letter_frequencies(ciphertext)[:10])
    print("Common ciphertext digrams:", ngram_frequencies(ciphertext, 2)[:10])
    print("Common ciphertext trigrams:", ngram_frequencies(ciphertext, 3)[:10])
    print("Repeated word shapes:", repeated_word_patterns(ciphertext))
