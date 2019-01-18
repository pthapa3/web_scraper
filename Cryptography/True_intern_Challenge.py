# True Intern Challenge

# Challenge Problem: Single-character XOR Cipher

# The hex encoded string below has been XOR'd against a single 
# character
# 1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736

# Your goal is to create a working piece of code that finds the 
# correct key and decrypts the message. You can use the 
# programming language of your choice. Your code should
# include a method for programmatically determining the
# correct answer.

# Assumption: 
#     Original plain text was in English 

# Input:
#     Hex encoded string:
#     1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736

# Functions:
#     dec_to_hex (decimal):  Converts numbers in base 10 to base 16

#     hex_to_dec (hex_str): Converts numbers in base 16 to base 10

#     get_hex_hexpair(start, stop, step): Separates given xored hex 
#     string in pairs and stores it in a list which would be used 
#     later 

#     xor_cipher_key(hex_list): A brute force function that tries 
#     out all the possible keys from 0-255

#     decrypt (hex_list): Attempts to get the original message using 
#     key found from the xor_cipher_key

#     validate (plaintext): As the plaintext was assumed to be in 
#      English, this method sort of validates the decrypted 
#      plaintext is in fact in English using NLTK stopwords.

# Output:
#     Successful decryption: Original text message
#     Failed decryption:  'Decryption Failed' message

from nltk.corpus import stopwords

xored_text = '1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736'

keywords = set(stopwords.words('english'))

# Change unicode to string
keywords = [str(x) for x in keywords]

def dec_to_hex(decimal):
	hex_num = []
	while (decimal > 0):
		hex_num.append(decimal%16)
		decimal = decimal/16
	hex_num = list(reversed(hex_num))
	hex_string = ''

	for h in hex_num:
		hex_string += format(h, 'x')
	return hex_string


hex_dict = {'0':0, '1': 1, '2' : 2,  '3': 3, '4' : 4, '5': 5, '6' : 6, '7': 7, 
	    '8' : 8, '9': 9, 'A' : 10, 'B': 11, 'C' : 12, 'D': 13, 'E' : 14, 
	    'F': 15}


def hex_to_dec (hex_str):
	dec_int = 0
	for i in range(len(hex_str)):
		dec_int = dec_int +  hex_dict.get(hex_str[len(hex_str)-(i+1)].upper()) * (16)**i
	return dec_int


# split the xored hex into pairs
def get_hexpair(start, stop, step ):
	hexpair_list = []
	for idx in range(start, stop, step):
		hexpair = ''
		hexpair = xored_text[idx] + xored_text [idx+1]
		hexpair_list.append(hexpair)
	return hexpair_list

hex_pair_list = get_hexpair(0, len(xored_text)-1, 2)

# Brute force xor cipher key
def xor_cipher_key(hex_list):
	ciph_key = 0
	freq_pair = max(hex_list, key=hex_list.count)
	for k in range(255):
		for p in hex_list:
			hx_pair_dec = hex_to_dec(str(p))
			if p == freq_pair and ((hx_pair_dec) ^ (k) == ord(' ')):
				ciph_key = k
				break
	return 	ciph_key	


c_key = xor_cipher_key(hex_pair_list)
def decrypt(hex_list):
	plain_text = ''
	for p in hex_list:
		hx_pair_dec = hex_to_dec(str(p))
		plain_text += chr((hx_pair_dec) ^ (c_key))
	return plain_text	



def validate (plain_txt):
	if any(words in plain_txt.split()  for words in keywords):
		return plain_txt
	else:
		return 'Decryption Failed!'


decrypted_text = validate(decrypt(hex_pair_list))

print '\nxored hex:', xored_text

print '\nxor cipher key:', c_key

print '\nOriginal text:', decrypted_text





# OUTPUT:

# xored hex: 1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736

# xor cipher key: 88

# Original text: Cooking MC's like a pound of bacon














