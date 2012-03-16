'''
Copyright (c) 2012, Simon Aquino
All rights reserved.

Made available under the BSD license - see the LICENSE file 
'''
#Imports
import sys
import random

# File specs
INPUT_PATH = '../inputs/'
NO_FILE = 'input%d.mr'

#Number specs
MAX_NO_FILES = 5
MAX_NUMS_IN_FILES = 100
MAX_RANGE_IN_FILES = 1000

WELCOME_MESSAGE='''
Hello and welcome to Simon's awesome Python script to generate random inputs
for a MapReduce problem.

Please select what you'd like to do.
'''

LIST_MAIN='''
1. Generate list of random numbers as input for a MapReduce problem.
2. Exit
'''
NO_FILES = '''
Please, specify how many files you want me to generate.
In case you select random, no more than 5 files will be generated.
Enter x for random or an integer: 
''' 

THANKS = 'Thanks! :)'

NUMS_IN_FILES = '''
Please, now specify how many numbers you wish each file to contain.
In case you select random, no more than 100 numbers will be generated.
Enter x for random or an integer: 
'''

RANGE_IN_FILES = '''
Now specify what is the range (+r,-r) in which the numbers should be generated.
In case you select random, the range won't be larger than (+1000, -1000).
Please give me r: '''

GREETINGS = 'Thanks for having used this program. Good bye!'

GENERATING_FILES = '''
Generating %d files, %d numbers per file and range(-%d,%d).
Please wait.....
'''

ALL_DONE = 'All done! You can find your files in %s' % (INPUT_PATH)

CHOICE = 'Please, make your choice here: '

def GenerateFile(no_files='x', nums_in_files='x', range_in_files='x'):
    '''Takes parameters and generate input files for a map reduce problem.
    
    Args:
        no_files        :    Number of files the user wishes to generate
        nums_in_files   :    How many numbers each file should generate.
        range_in_file   :    The range (-r,+r) that includes the numbers
                             generated.
    '''
    
    # Initialise randomiser using sys time
    random.seed()
    
    if (no_files == 'x'):
        # Generate random value between 1 and 5.
        no_files = random.randint(1, MAX_NO_FILES)
    else:
        no_files = int (no_files) 
    
    if (nums_in_files == 'x'):
        # Generate random value between 1 and 100.
        nums_in_files = random.randint(1, MAX_NUMS_IN_FILES)
    else:
        # Generate random values between 1 and 1000.
        nums_in_files = int (nums_in_files)

    if (range_in_files == 'x'):
        range_in_files = random.randint(1,MAX_RANGE_IN_FILES)
    else:
        range_in_files = int (range_in_files)
    
    print GENERATING_FILES % \
        (no_files, nums_in_files, range_in_files, range_in_files)
    
    for job in range(0, no_files):
        
        content = GenerateNumbers(nums_in_files, range_in_files)
        
        filename = NO_FILE % (job)
        file_path = INPUT_PATH + filename
        
        # Open file.
        f = open(file_path, 'w')
        
        # Write content
        f.write(content)
        
        # Close file
        f.close()
    
    print ALL_DONE
        

def GenerateNumbers(nums_in_file, range_in_file):
    '''Takes parameters and generates a string of random numbers
       separated by a comma ','
       
       Args:
            nums_in_files   :    How many numbers each file should generate.
            range_in_file   :    The range (-r,+r) that includes the numbers
                                 generated.
    '''
    
    string = ''
    
    for i in range(0, nums_in_file):
        # Don't append comma at the end of file!
        if (i==nums_in_file-1):
            string += str(random.randint(-range_in_file,range_in_file)) 
        else:
            string += str(random.randint(-range_in_file,range_in_file)) + ','

    return string
        

def UserDecides():
    # Ask user what to do first
    
    print LIST_MAIN
    
    user_choice = input(CHOICE)
    
    if not (user_choice == 1):
        print GREETINGS
        sys.exit(0)

    no_files = raw_input(NO_FILES)
    
    print THANKS
    
    nums_in_files = raw_input(NUMS_IN_FILES)
    
    print THANKS
    
    range_in_files = raw_input(RANGE_IN_FILES)
    
    print THANKS
    
    return no_files, nums_in_files, range_in_files
    

def main():
    print WELCOME_MESSAGE
    
    nf, nif, rif = UserDecides()
    GenerateFile(nf, nif, rif)
    
    print GREETINGS


if __name__ == '__main__':
    main()