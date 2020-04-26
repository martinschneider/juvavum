.model small
.data
    errorMessage DB "Invalid input. Usage: board number (0 <= number < 65536)", "$"
.code

;
; Each field of a 4 x 4 grid is represented by a power of 2:
;
; 2^0  2^1  2^2  2^3
; 2^4  2^5  2^6  2^7
; 2^8  2^9  2^10 2^11
; 2^12 2^13 2^14 2^15
;
; The sum of these powers is the numerical representation of the board.
;
;
; This program prints the board represented by the number passed as its argument.
;
; For example, board 1000 will print:
;
; - - - X
; - X X X
; X X - -
; - - - -
;
; This is because 1000 = 2^3 + 2^5 + 2^6 + 2^7 + 2^8 + 2^9.
;

main proc
    call parseNumber                ; convert command argument to integer and store it as a word at [81h]
    call printBoard                 ; print the board to stdout
    mov ah,4ch                      ; exit
    mov al,0
    int 21h
main endp

; reads a number from the (only) command argument and writes its value back to [81h]
parseNumber proc
    xor bx,bx                       ; empty bx
    mov bl,byte ptr es:[80h]        ; bl = length of command arguments
    cmp bl,6                        ; leading whitespace + 5 digits for largest possible 16-bit value
    jg printError
    dec bx
    mov cl,bx                       ; cl = number of characters
    mov si,81h
    xor ch,ch                       ; clear ch
    add si,cx                       ; si points at the least significant bit
    xor bx,bx
    mov bp,1
repeat:
    mov al,[si]                     ; store the current character in al
    cmp al,48                       ; if ascii code < 48 or > 57 -> error
    jl printError
    cmp al,57
    jg printError
    sub al,48                       ; convert ascii to digit
    xor ah,ah                       ; clear ah
    mul bp                          ; ax *= bp
    add bx,ax
    jc printError                   ; input > 65535 results in a carry -> error
    mov ax,bp
    mov bp,10
    mul bp                          ; ax *= bp
    mov bp,ax
    dec si
    loop repeat
    mov si, 81h
    mov [si], bx                    ; write the board representation to memory
    ret
printError:
    mov ax,@data
    mov ds,ax
    mov ah,09h                      ; print string
    lea dx,errorMessage
    int 21h
    mov ah,4ch                      ; exit
    mov al,1                        ; exit code 1
    int 21h
parseNumber endp

; prints the board
printBoard proc
    mov cx,0                        ; loop counter
printLoop:
    mov ax,cx
    mov bl,4
    div bl                          ; ah = ax % bl
    cmp ah,0                        ; if ah == 0
    je newLine
startLoop:
    mov ax,4                        ; board width
    mov bx,4                        ; board height
    mul bx                          ; total number of fields
    cmp cx,ax                       ; if cx >= ax
    jge exit
    mov ax,1
    mov dx,0
shift:
    cmp dx,cx                       ; if dx >= cx
    jge next
    shl ax,1                        ; ax << 1
    inc dx                          ; dx++
    jmp shift
next:
    mov bx,word ptr es:[81h]        ; load board from [81h]
    and bx,ax                       ; bx &= ax
    cmp bx,0                        ; if ax != 0
    jne printFilled
    mov dx,"-"                      ; empty field
    jmp print
printFilled:
    mov dx,"X"                      ; occupied field
print:
    mov ah,02h                      ; print character
    int 21h
    mov dx," "                      ; whitespace
    int 21h
    inc cx                          ; cx++
    jmp printloop
newLine:
    mov ah,02h                      ; print character
    mov dx,13                       ; cr
    int 21h
    mov dx,10                       ; lf
    int 21h
    jmp startLoop
exit:
    ret
printBoard endp
end main