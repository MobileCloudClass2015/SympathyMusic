source = open("sample.txt", 'r')
target = open("feature.txt", 'w')

data = source.read().split('\n')

for i in range(0, len(data)):
	target.write(data[i])

source.close()
target.close()
