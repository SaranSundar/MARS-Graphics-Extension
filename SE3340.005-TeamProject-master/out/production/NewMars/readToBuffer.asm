#reads sets of 3 digit numbers into a word array
#a0 file descriptor
#a1 write address
#a2 length
.globl readToBuffer
.data
buf:	.space 5
.text
readToBuffer:
#save to stack to keep preserved registers
addi $sp, $sp, -4
sw $s0, ($sp)
addi $sp, $sp, -4
sw $s1, ($sp)
addi $sp, $sp, -4
sw $s2, ($sp)
addi $sp, $sp, -4
sw $ra, ($sp)

move $s0, $a0#file descriptor
move $s1, $a1#write
move $s2, $a2#length
loop:
move $a0, $s0	# $a0 - file descriptor to read from
la $a1, buf	# $a1 - buffer to read into
li $a2, 3	# $a2 - always read 3 bytes/characters
li $v0, 14	# Syscall num for read from file
syscall		# Read

move $a0, $a1	# Give result to atoi
jal atoi	# atoi

#move $a0, $v0	# Print integer
#li $v0, 1
#syscall

sw $v0, ($s1)			# Put integer in write location
addi $s1, $s1, 4		# Increment array pointer
addi $s2, $s2, -1		#decrement length
bne $s2, $zero, loop		# If array not filled, get next known number

lw $ra, ($sp)
addi $sp, $sp, 4
lw $s2, ($sp)
addi $sp, $sp, 4
lw $s1, ($sp)
addi $sp, $sp, 4
lw $s0, ($sp)
addi $sp, $sp, 4
jr $ra