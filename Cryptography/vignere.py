
inputChar = 'ATTACKATDAWN'
key = 'LEMON'

alphabets = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'


alpha_dict = {}
key_dict = {}

for i in range(len(alphabets)):
	alpha_dict[alphabets[i]] = i


print 'plaintext', inputChar

print 'Key', key


def get_key_index (indx):
    if indx > len(key)-1:
    	return indx % (len(key))
    else:
		return indx 

def encrypt(plaintext, key_text):
	encrypt_txt = ''
	for index in range(len(plaintext)):
		
		encrypt_txt_index = (alpha_dict.get(plaintext[index]) + alpha_dict.get(key_text[get_key_index(index)])) % 26
		encrypt_txt += alphabets[encrypt_txt_index]
	return encrypt_txt


cipher_text = encrypt(inputChar, key)

print 'cipher_text', cipher_text



def decrypt(cipher_text, key_text):
	decrypted_txt = ''
	for index in range(len(cipher_text)):
		decrypt_txt_index = (alpha_dict.get(cipher_text[index]) - alpha_dict.get(key_text[get_key_index(index)])) % 26
		decrypted_txt += alphabets[decrypt_txt_index]
	return decrypted_txt


decrypt_text = decrypt(cipher_text, key)


print 'decrypt_text', decrypt_text