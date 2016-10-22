'''
Created on 2015-2-12

@author: Administrator
'''
import gzip
import sys, os
from nltk.tokenize import word_tokenize, sent_tokenize

def process_onefile(inputfile, outfile):
    infile = gzip.open(inputfile, "r")
    inline = infile.readline()
    while inline != '': ##blank lines == "\n", the end of the file == '' (which is NOT an exception)
        counter = 0
        bufferstring = ''
        while counter < 500 and inline != '':
            while inline.strip().lower() != '<text>' and inline.strip() != '':
                inline = infile.readline().lower()
            inline = infile.readline().lower()
            while inline.strip().lower() != '</text>' and inline.strip() != '':
                if inline.strip().lower() != '<p>' and inline.strip().lower() != '</p>':
                    bufferstring = ''.join([bufferstring, inline])
                    inline = infile.readline()
                    counter += 1
                else:
                    inline = infile.readline().lower()
        bufferstring = bufferstring.replace("\n\n", "\n")
        bufferstring = bufferstring.replace("\n", " ")
        #print bufferstring
        try:
            buffersents = sent_tokenize(bufferstring)
            for sent in buffersents:
                buffsentwords = word_tokenize(sent)
                cleanwords = []
                for word in buffsentwords:
                    word = word.replace("(", "-LRB-")
                    word = word.replace(")", "-RRB-")
                    word = word.replace("{", "-LCB-")
                    word = word.replace("}", "-RCB-")
                    word = word.replace("/", "-SLASH-")
                    word = word.replace("*", "-STAR-")
                    cleanwords.append(word)
                cleanstring = ' '.join(cleanwords)
                #print cleanstring
                outfile.write(cleanstring)
                outfile.write('\n')
        except:
            pass
        inline = infile.readline().lower()
    infile.close()
        
if __name__ == '__main__':
    file_output=open(sys.argv[2], "w")
    count = 0
    for root,dirs,files in os.walk(sys.argv[1]):
        for filespath in files:
            if filespath.endswith(".gz") :
                process_onefile(os.path.join(root,filespath), file_output)
                count = count+1
                print count
                if count%50 == 0  :
                    print "\n"
                else :
                    print " "
    
    file_output.close()
    pass