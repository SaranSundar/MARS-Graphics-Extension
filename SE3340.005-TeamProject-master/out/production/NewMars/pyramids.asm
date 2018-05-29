.globl main
.data
#Querys
coordsQuery: .asciiz "Input Location: "
inputQuery: .asciiz "\nInput Value: "
#Messages
inputProblemMessage: .asciiz "\nThere was a problem with the input.\n"
winMessage: .asciiz "You Win!\n"
wrongMessage: .asciiz "Wrong! Try again!\n"
fileOpenErrorMessage: .asciiz "The file failed to open.\n"
#Input Buffers
coordsInput: .asciiz "--"
valueInput: .asciiz "---"
#Pyramid Data
output: .asciiz "            -----\n            | AA|\n          ---------\n          | BA| BB|\n        -------------\n        | CA| CB| CC|\n      -----------------\n      | DA| DB| DC| DD|\n    ---------------------\n    | EA| EB| EC| ED| EE|\n  -------------------------\n  | FA| FB| FC| FD| FE| FF|\n-----------------------------\n| GA| GB| GC| GD| GE| GF| GG|\n-----------------------------\n"
.align 2#this is here so that the next two spaces (values and known values) are word aligned
values:	.space 112 #values currently on the pyramid
selectionBuffer: .space 172
knownValues: .space 112 #reference fully completed pyramid
writeAddress: .word 31 #This is the location in the ascii output where the numbers should be written
.word 67, 71, 
.word 107, 111, 115, 
.word 151, 155, 159, 163, 
.word 199, 203, 207, 211, 215, 
.word 251, 255, 259, 263, 267, 271, 
.word 307, 311, 315, 319, 323, 327, 331

file: 	.asciiz "source.txt"

.text
main:
li $a1, 0 #@MUSIC-START
#----load values from disk----
# Open file in read mode
la $a0, file
li $a1, 0
li $a2, 0
li $v0, 13
syscall
move $s0, $v0	#File descriptor in $s0 now
#check to make sure the file was opened correctly
bge $s0, $zero, fileOpenedSuccessfully
la $a0, fileOpenErrorMessage
li $v0, 4
syscall
li $v0, 10
syscall
fileOpenedSuccessfully:
li $a1, 100 #Upper bound of random number
li $v0, 42
syscall
move $s5, $a0 #Holds the random number
selectRandom:
# Generates a random number from 0-99 and skips that number of entries in the source file. 
addi $s5, $s5, -1
move $a0, $s0
la $a1, selectionBuffer
li $a2, 170
li $v0, 14
syscall
bgez $s5, selectRandom

# Read all of the first line into knownValues
move $a0, $s0
la $a1, knownValues
li $a2, 28
jal readToBuffer

#Obtain single new line character
move $a0, $s0
la $a1, coordsInput #write no-where/ unused
li $a2, 1
li $v0, 14
syscall

# Read all of second line into values
move $a0, $s0
la $a1, values
li $a2, 28
jal readToBuffer

#----Populate the picture full of Values----
populateinit:
move $s1, $zero#s1 is the offset we are on
li $s3, 112#s3 the number of spots (28*4)

populateLoop:


lw $a0, values($s1)#a0 is the loaded word
beq $a0, $zero, incrementpopulateloop

#write to screen
move $a1, $s1 #a0 is the value. a1 is the location starting from the top
add $t0, $t0, $zero #@DRAW

#write to ascii output
lw $t0, writeAddress($s1)
la $a1, output($t0)#total output address
jal int2ascii

incrementpopulateloop:
addi $s1, $s1, 4#increment
beq $s1, $s3, endpopulateloop
j populateLoop
endpopulateloop:

#----Start of loop. Failed input jumps here to after printing an error message so you can try again----
loop:
#----Print Status----
la $a0, output
li $v0, 4
syscall
#----Check if we won----
li $a0, 28#28 spots to check
la $a1, values#current pyramid
la $a2, knownValues#reference pyramid
jal compareArrays
beq $v0, $zero, win
#----Get Input Letters----
li $v0, 4#print String
la $a0, coordsQuery
syscall
li $v0, 8#readString
la $a0, coordsInput
li $a1, 3
syscall

la $a0, coordsInput
jal validLetters#convert to address
blt $v0, $zero, loop #if negative, then there was an input problem

move $s0, $v0#s0 is the write index

#----Get Input Numbers----
li $v0, 4#print String
la $a0, inputQuery
syscall
li $v0, 8#read String
la $a0, valueInput
li $a1, 4
syscall
#----Convert input to decimal----
la $a0, valueInput
jal atoi
beq $v1, $zero, noProblem
#otherwise we have a problem with our input
li $v0, 4
la $a0, inputProblemMessage
syscall
j loop
noProblem:
#----Check Input----
move $s1, $v0#s1 is now the number read in
sll $t0, $s0, 2
lw $t1, knownValues($t0)
beq $s1, $t1, correctInput
#incorrect input
li $v0, 4#print String
la $a0, wrongMessage
syscall

move $t7, $v0
li $a0, 40        # Set wrong indicator pitch
li $a1, 175        # Time duration of wrong sound in ms
jal playsound        # Play "wrong" sound
jal playsound
move $v0, $t7

j loop
correctInput:
#----Save value to the list of values----
move $t7, $v0
li $a0, 70        # Set right indicator pitch
li $a1, 200        # Time duration of right sound in ms
jal playsound        # Play "right" sound	
jal playsound
move $v0, $t7

sw $v0, values($t0) #save input to values list to replace a -1
#----Add input to image----

move $a0, $v0

move $a1, $t0 #same format as above
add $t0, $t0, $zero #@DRAW

lw $t0, writeAddress($t0) #t0 is now the address to write at from output
la $a1, output
add $a1, $a1, $t0
jal int2ascii

j loop
win:
la $a0, winMessage
li $v0, 4
syscall

# Winning Jingle
li $a0, 70        # Set right indicator pitch
li $a1, 100       # Time duration of right sound in ms
jal playsound     # Play note of winning jingle sound	
addi $a0, $a0, 2
jal playsound
addi $a0, $a0, 2
jal playsound
addi $a0, $a0, 2
jal playsound
addi $a0, $a0, 2
jal playsound
addi $a0, $a0, 2
li $a1, 250
jal playsound
jal playsound
jal playsound
jal playsound
jal playsound
li $a1, 2000
jal playsound

li $a1, 0 #@MUSIC-STOP
li $v0, 10
syscall


# ========================================================================== #
# 			    --- Play Sound Subroutine ---                    #
# ========================================================================== #
#	Param - argument $a0 for pitch, 0-127
#		argument $a1 for duration in milliseconds
#		arguments $a2 and $a3 are set by this subroutine
playsound:
li $a2, 120	# Instrument used (0-127)
li $a3, 127	# Volume (0-127)
li $v0, 33	# Syscall for play sound and stall program for duration
syscall		# Execute syscall
jr $ra		# Jump back to call point
