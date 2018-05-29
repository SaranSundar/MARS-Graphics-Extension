#this function checks if two arrays contain identical numbers. Used to check if we won.
.globl compareArrays
#a0 length of the arrays
#a1 address of the first array
#a2 address of the first array
#v0 0 if the same, 1 if different.
compareArrays:
beq $a0, $zero, same#if the arrays are 0 length, then they are the same
lw $t0, ($a1)#load values
lw $t1, ($a2)
bne $t0, $t1, diff#if any are not equal, then they are different
addi $a0, $a0, -1#shorten the length
addi $a1, $a1, 4#increment the pointers
addi $a2, $a2, 4
j compareArrays#stack-free recursion!!!

same:
li $v0, 0
jr $ra
diff:
li $v0, 1
jr $ra