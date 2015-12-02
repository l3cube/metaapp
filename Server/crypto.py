# Python imports
import base64

# Third-Party imports
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.exceptions import InvalidSignature
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.serialization import load_der_public_key
from cryptography.hazmat.primitives.serialization import load_der_private_key

def generate_rsa_keypair(bits=4096):
    return rsa.generate_private_key(
        public_exponent=65537,
        key_size=bits,
        backend=default_backend()
    )


def export_private_key(private_key):
    pem = private_key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.TraditionalOpenSSL,
        encryption_algorithm=serialization.NoEncryption()
    )

    return pem


def export_public_key(public_key):
    pem = public_key.public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    )

    return pem


def load_private_key(private_key_pem_export):
    print private_key_pem_export
    private_key_pem_export = (bytes(private_key_pem_export, encoding='utf8') if not isinstance(private_key_pem_export, bytes) else private_key_pem_export)

    return serialization.load_pem_private_key(
        private_key_pem_export,
        password=None,
        backend=default_backend()
    )


def load_public_key(public_key_pem_export):
    public_key_pem_export = (bytes(public_key_pem_export, encoding='utf8')
                             if not isinstance(public_key_pem_export, bytes) else public_key_pem_export)

    return serialization.load_pem_public_key(
        data=public_key_pem_export,
        backend=default_backend()
    )


def load_private_key_string(prikey):
    b64data = '\n'.join(prikey.splitlines()[1:-1])
    derdata = base64.b64decode(b64data)
    key = load_der_private_key(derdata,None,default_backend())
    return key


def load_public_key_string(pubkey):
    b64data = '\n'.join(pubkey.splitlines()[1:-1])
    derdata = base64.b64decode(b64data)
    key = load_der_public_key(derdata,default_backend())
    return key


def encrypt_message(public_key, message):
    message = bytes(message)
    ciphertext = public_key.encrypt(message,padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA1()),algorithm=hashes.SHA1(),label=None))
    ciphertext = base64.b64encode(ciphertext)
    return ciphertext


def decrypt_message(private_key, ciphertext):
    ciphertext = ciphertext.replace('-', '+');
    ciphertext = ciphertext.replace('_', '/');
    ciphertext = base64.b64decode(ciphertext)
    plaintext = private_key.decrypt(ciphertext,padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA1()),algorithm=hashes.SHA1(),label=None))
    return plaintext

def sign_data(private_key, data):
    data = bytes(data, encoding='utf8') if not isinstance(data, bytes) else data

    signer = private_key.signer(
        padding.PSS(
            mgf=padding.MGF1(hashes.SHA256()),
            salt_length=padding.PSS.MAX_LENGTH
        ),
        hashes.SHA256()
    )

    signer.update(data)
    signature = signer.finalize()
    return str(base64.b64encode(signature), encoding='utf8')


def verify_sign(public_key, signature, data):
    try:
        data = bytes(data, encoding='utf8') if not isinstance(data, bytes) else data
        signature = base64.b64decode(signature) if not isinstance(signature, bytes) else signature


        verifier = public_key.verifier(
            signature,
            padding.PSS(
                mgf=padding.MGF1(hashes.SHA256()),
                salt_length=padding.PSS.MAX_LENGTH
            ),
            hashes.SHA256()
        )

        verifier.update(data)
        verifier.verify()
        return True

    except InvalidSignature:
        return False
  



