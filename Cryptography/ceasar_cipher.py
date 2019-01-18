
inputChar = 'JUMPS'
key = -3

alphabets = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'


alpha_dict = {}
key_dict = {}

for i in range(len(alphabets)):
	alpha_dict[alphabets[i]] = i


print 'plaintext', inputChar

def encrypt(plaintext, shift):
	encrypt_txt = ''
	for index in range(len(plaintext)):
		encrypt_txt_index = (alpha_dict.get(plaintext[index]) + key) % 26
		encrypt_txt += alphabets[encrypt_txt_index]
	return encrypt_txt


cipher_text = encrypt(inputChar, key)

print 'cipher_text', cipher_text



def decrypt(cipher_text, key_text):
	decrypted_txt = ''
	for index in range(len(cipher_text)):
		decrypt_txt_index = (alpha_dict.get(cipher_text[index]) - key) % 26
		decrypted_txt += alphabets[decrypt_txt_index]
	return decrypted_txt


decrypt_text = decrypt(cipher_text, key)


print 'decrypt_text', decrypt_text