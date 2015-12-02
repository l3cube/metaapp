# Python imports
import base64

# Third-Party imports
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.exceptions import InvalidSignature
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric.rsa import RSAPublicNumbers
from cryptography.hazmat.primitives.serialization import load_der_public_key
from cryptography.hazmat.primitives.serialization import load_der_private_key
from cryptography.fernet import Fernet


private_key = rsa.generate_private_key(65537,2048,default_backend())
pem=private_key.private_bytes(serialization.Encoding.PEM,serialization.PrivateFormat.TraditionalOpenSSL,serialization.NoEncryption())
print pem

public_key = private_key.public_key()
pem=public_key.public_bytes(serialization.Encoding.PEM,serialization.PublicFormat.SubjectPublicKeyInfo)
print pem

with open("private.pem", "rb") as key_file:
	pubkey=key_file.read()
b64data= '\n'.join(pubkey.splitlines()[1:-1])
derdata=base64.b64decode(b64data)
key=load_der_private_key(derdata,None,default_backend())
print key

with open("public.pem", "rb") as key_file:
	pubkey=key_file.read()
b64data= '\n'.join(pubkey.splitlines()[1:-1])
derdata=base64.b64decode(b64data)
key=load_der_public_key(derdata,default_backend())
print key

message = 't1IUyLvj9qPyvo9zDpYJYCCK6QFd19I1gg4zAOMwifFQCR2wCWQPL5cQDK1BtR22hIPx9XJ3rdMc\nFArI5b-VqoXK1W42t0XlYPeDa3BSRbF3UiIOzcWub92sashY5c5EMZK_p_ZT_DPkgZwse00NUtEe\nm0s8shq-idIOEQroxjzgftQn3NfOT58S1tnTKsNY-sWP916jnhlMhO8HdAi7iEp319Dm9Taf0y5P\nZHrVsqp9qzbCmHk-GgVTHUBfDANo5WpdadBKX-8ppBL5RQDPkO1yBnIOSKfzaCbLaQPrEoG0e22t\n67gfhleGHCtmT0ay3OWIXkFtv1DalrPjlxUEj9lxRTGxUtNj6d-_Ky2Hzp8EIAgguM6Xn5hKEk8k\nXxDTyAYqU9Ucwb29SPVb7L_Bda9A3QamV4Rcvv-7nWVgh6A=\n'
seed = 'Aes seed value'
key = base64.b64decode(seed)
f = Fernet(key)
plaintext = f.decrypt(message)
print(plaintext)
