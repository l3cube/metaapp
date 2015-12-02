import base64
import hashlib
from Crypto import Random
from Crypto.Cipher import AES


class AESCipher:

    def __init__(self, key):
        self.bs = 16
        self.key = hashlib.sha256(key.encode()).digest()

    def encrypt(self, message):
        # message = self._pad(message)
        iv = bytes(b"0000000000000000")
        print iv
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return base64.b64encode(iv + cipher.encrypt(message)).decode('utf-8')

    def decrypt(self, enc):
        enc = base64.b64decode(enc)
        iv = enc[:AES.block_size]
        # iv = bytes(b"0000000000000000")
        print iv
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return (cipher.decrypt(enc[AES.block_size:])).decode('utf-8')

    def _pad(self, s):
        return s + (self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)

    @staticmethod
    def _unpad(s):
        return s[:-ord(s[len(s)-1:])]

print AES.block_size
token = 'XTpa71ZBuaWg-DgkL07bi3hgAjUpEBLYA3VCDsBZJiNkD5wbzGqYSs0dmHe1RdJf_L91coJVayQ-6hUahiAlYeGUGUGW1V5Hdlig4dZLIhnZDi0gHHB2-AwVBBef2nFutual697ONFWDRg2X5xTdwjznG00KO4YPOlyiCAo4D0fmzsYoKFKT3R-H9nJ7wQBGj8UEbS1dtltdjp-Mj36KRVq5UC3XdnMRxINtVOt3H9K2cgKnfR2zfVdLy99ZbWv3atgORHLkq7l3_9NugkEuMyPSuGkDSati_RnbKHtwZph3rmEKJSjgumiSsNMfxC05cC2sPb-UiRTylDcNDeN3BPsfwkKVeNQKlDxC5BmCftYTb_hBfRoY5Ee1Q2QzFaAmqRNcfV8F5j0gZHZucrKYQUX_4WI3QZmkYtAk9nqMm5w='
token = token.replace('-', '+')
token = token.replace('_', '/')
print(token)
print(len(token))
password = "Aes seed value"
message='{"UUID":"94634","Pubkey":"-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC\/aFBH4ETwEkiDYAdbAF7Nypr5\nyKqGf1Or3fIQJA4S4B21hG10EJhI8rujXfqgmoWEMdB0dO553a8TW6lGchS2Eqni\nAXoZf\/RDkOmAR9sD5l0\/bSO2hTszDqnsKkHEuRyq5\/6q7ihi2K1wfM96y9ar5c6r\nnNJuCFSmyP2DnHiZSwIDAQAB\n-----END PUBLIC KEY-----\n"}'
obj = AESCipher(password)
em = obj.encrypt(message)
print em
print(len(em))
# em = token
m = obj.decrypt(em)
print m
