#this function operates similarly to c's atoi function.
.globl atoi
.data
errormsg: .asciiz "error in atoi"
.text
#ARGUMENTS
#a0: pointer to null terminated string (input)
#RETURN
#v0: entered number
#v1: error (boolean)

atoi:
move $v0, $zero #sum
move $v1, $zero #error bit
li $t1, 10 #multiplier/new line char
li $t2, 32 #space character
loop:
lbu $t0, ($a0) #load this digit
beq $t0, $zero, end #end if this is the null terminator
beq $t0, $t1, end #end if this is the new line
beq $t0, $t2, next #skip space characters
mult $v0, $t1 #multiply the base by 10
mflo $v0
addi $t0, $t0, -48 #convert from ascii to decimal
blt $t0, 0, error #error if less than 0 or greater than 9
bgt $t0, 9, error
add $v0, $v0, $t0 #add the value to the sum
next:
addi $a0, $a0, 1 #increment the string pointer to the next digit
j loop
error:
li $v1, 1
end:
jr $ra
