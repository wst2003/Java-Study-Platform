file = open('answers','r',encoding='utf-8')
i = 1
for c in file.read():
    if c=='T'or c=='F':
        print(f'insert judgequestions values(11,\'判断题\',{i},\'{c}\');')
        i+=1