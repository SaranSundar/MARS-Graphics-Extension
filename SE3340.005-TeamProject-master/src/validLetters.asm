#this function determines if the two letters are a valid location on the pyramid, and returns the index
.globl validLetters
#a0 address to the two letters
#v0 -1 if error. 0-27 is the index of the letter pair.
.data
badLettersMessage: .asciiz "\nThose letters are not on the grid. Good input example: AA (case sensitive)\n"
rowIndex: .byte 0, 1, 3, 6, 10, 15, 21 #these are row offsets used to calculate the write address
.text
validLetters:

lbu $t0, ($a0)#load letters
lbu $t1, 1($a0)
addi $t0, $t0, -65
addi $t1, $t1, -65

#check here if t1 > t0 or t0 > height of pyramid or either is negative.
blt $t0, $zero, badLetters#check if either character points to a negative row/column
blt $t1, $zero, badLetters
blt $t0, $t1, badLetters#if the second one is bigger
li $t2, 6#maximum letter
bgt $t0, $t2, badLetters # first letter is greater than pyramid size

#calculate the index
lb $t2, rowIndex($t0)
addu $v0, $t1, $t2

j goodLetters
badLetters:
li $v0, 4#print String
la $a0, badLettersMessage
syscall
li $v0, -1
goodLetters:
jr $ra
