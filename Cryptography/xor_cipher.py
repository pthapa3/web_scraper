
import binascii, sys
from nltk.corpus import stopwords

data = '7b5a4215415d544115415d5015455447414c155c46155f4058455c5b523f'

keywords = set(stopwords.words('english'))

keywords = [str(x) for x in keywords]


def dec_to_bin (decimal_num, num_bits):
	binary_num = []
	while (decimal_num > 0):
		if decimal_num % 2 == 0:
			binary_num.append(0)
		else:
			binary_num.append(1)
		decimal_num = decimal_num/2

	while (len(binary_num) < num_bits):
			binary_num.append(0)
	return list(reversed(binary_num))


# print 'binary of 9 with 8 bits', ''.join(str(b) for b in dec_to_bin(10, 8))

# print 'binary of 83 with 8 bits', ''.join(str(b) for b in dec_to_bin(83, 8))


def string_to_bin(strings):
	string_list = []
 	for s in strings:
 		string_list = string_list + dec_to_bin(ord(s), 8)
 		
 	return string_list

# print 'ord C', ord('B')
# print 'ord S', ord('S')

# print 'converting', 'BS', 'to bin', ''.join(str(x) for x in string_to_bin ('BS'))




def bin_to_dec (bin_str):
	dec_int = 0
	for i in range(len(bin_str)):
		dec_int = dec_int + int(bin_str[len(bin_str) - (i+1)]) * 2**i
	return dec_int


# print 'bin_to_dec of 11 is', bin_to_dec ('11')




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


# print 'dec_to_hex of 479 is', dec_to_hex(479)


hex_dict = {'0':0, '1': 1, '2' : 2,  '3': 3, '4' : 4, '5': 5, '6' : 6, '7': 7, '8' : 8, '9': 9, 'A' : 10, 'B': 11, 'C' : 12, 'D': 13, 'E' : 14, 'F': 15}


def hex_to_dec (hex_str):
	dec_int = 0
	for i in range(len(hex_str)):
		dec_int = dec_int +  hex_dict.get(hex_str[len(hex_str)-(i+1)].upper()) * (16)**i
		
	return dec_int




# print 'hex_to_dec of 1b is ', dec_to_bin (hex_to_dec ('F'), 8)

# print 'hex_to_dec of 1b is ', dec_to_bin (hex_to_dec ('10'), 8)



# hex_in_string = ''.join(str(d) for d in dec_to_bin (hex_to_dec ('37'), 8))
hex_in_string = hex_to_dec ('1b')
print hex_in_string
# print type(hex_in_string)



# cipher_key = ''.join (str(b) for b in string_to_bin('X'))

cipher_key = bin_to_dec(string_to_bin('X'))

# print cipher_key

# print (hex_in_string) ^ 59




def get_hexpair(start_indx, stop, step ):
	hexpair_list = []
	for idx in range(start_indx, stop, step):
		hexpair = ''
		hexpair = data[idx] + data [idx+1]
		hexpair_list.append(hexpair)
	return hexpair_list

hex_pair_list = get_hexpair(0, len(data)-1, 2)





def get_cipher_key(hex_list):
	ciph_key=0
	for k in range(255):
		for p in hex_list:
			check = hex_to_dec(str(p))
			if p == '78' and ((check) ^ (k) == 32):
				print p, k
				ciph_key = k
				break
	return 	ciph_key	



def decrypt(hex_list):
	plain_text = ''
	c_key = get_cipher_key(hex_list)
	for p in hex_list:
		check = hex_to_dec(str(p))
		plain_text += chr((check) ^ (c_key))
	return plain_text	


def validate (plain_txt):
	if any(words in plain_txt.split()  for words in keywords):
		return plain_txt
	else:
		return 'Decryption Failed!'
		


def has_necessary_percentage_frequent_characters( text, p=38 ):
    most_frequent_characters = list("etaoin")
  
    cnt = 0
    for char in most_frequent_characters:
        cnt += text.count(char)
      
    percent_characters =  float(cnt)*100/len(text)
   
    print 'percent', percent_characters
    
    if (percent_characters < p):
        return False
    return True


decrypted_text = validate(decrypt(hex_pair_list))

if has_necessary_percentage_frequent_characters(decrypted_text):
	print decrypted_text





key = hex_to_dec(max(hex_pair_list, key=hex_pair_list.count)) ^ ord (' ')
print 'key', key




for pr in hex_pair_list:
	# print hex_to_dec(pr), pr
	print chr (hex_to_dec(pr) ^ 53)



print len(bytearray(data))

most_frequent_characters = list("etaoin")


print most_frequent_characters




