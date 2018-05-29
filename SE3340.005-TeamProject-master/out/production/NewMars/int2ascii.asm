#this function creates an ascii string from an integer. (without trailing null) (with padding to 3)
.globl int2ascii
.text
#$a0: input integer
#$a1: pointer to ascii output
#$v0: length of output

int2ascii:

#pad out with spaces
li $t0, 32
sb $t0, 0($a1)
sb $t0, 1($a1)
sb $t0, 2($a1)

li $t0, 10
li $v0, 0
move $t3, $a1 #last character
loop:
div $a0, $t0
mflo $a0 #divided by 10
mfhi $t1 #t1 is the remainder
addi $t1, $t1, 48 #change to ascii number
sb $t1, ($t3) #store the character
addi $v0, $v0, 1 #increment the length
beq $a0, $zero, switch #if a0 is 0 then that means we're done
addi $t3, $t3, 1 #increment the end
j loop

switch:#switch the characters since they were entered backwards
lbu $t0, ($a1)#load
lbu $t1, ($t3)
sb $t1, ($a1)#save opposite
sb $t0, ($t3)
addi $a1, $a1, 1#move the ends together
addi $t3, $t3, -1
bgt $t3, $a1, switch #as long as t3 is greater than a1, we need to keep moving closer

jr $ra
