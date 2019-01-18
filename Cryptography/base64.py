base_64_table = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'

base_64_dict ={}

for i in range(len(base_64_table)):
	base_64_dict[base_64_table[i]] = i

text = 'Hey'


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

print ''.join(str(s) for s in string_to_bin('.'))

def sep_into_three(inputstring):
	three_letter = []
	if len(inputstring) < 3:
		three_letter.append(inputstring)
	else:
		for i in range(0, len(inputstring), 3):
			if i == len(inputstring)-1:
				print inputstring[i]
				string_bin = ''.join(str(s_bin) for s_bin in (string_to_bin(inputstring[i])))
				while (len(string_bin) != 24):
					string_bin += '0'

				print 's', string_bin
				three_letter.append(inputstring[i])
			elif i+1 == len(inputstring)-1:
				print inputstring[i],inputstring[i+1]
				string_bin = ''.join(str(s_bin) for s_bin in (string_to_bin(inputstring[i]+inputstring[i+1])))
				while (len(string_bin) != 24):
					string_bin += '0'

				print 's1', string_bin
				three_letter.append(inputstring[i]+inputstring[i+1])
			else:
				print inputstring[i], inputstring[i+1], inputstring[i+2]
				three_letter.append(inputstring[i]+inputstring[i+1]+inputstring[i+2])
	return three_letter

def sep_six_bits(inputstring):
	string_bin = ''
	string_bin_sep = []
	sep_letters = sep_into_three(inputstring)
	for s in sep_letters:
		string_bin += ''.join(str(s_bin) for s_bin in (string_to_bin(s)))
	print string_bin
	for i in range(0, len(string_bin), 6):
		if i == len(string_bin)-1:
			string_bin_sep.append(string_bin[i])
		else:
			string_bin_sep.append(string_bin[i:i+6:])
	print string_bin_sep

sep_six_bits ('owe m')






