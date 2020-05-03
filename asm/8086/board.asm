.model small
.data
    errorMessage db "Invalid input. Usage: board number (0 <= number < 65536)", "$"
    blue equ 1
    grid equ 7
.code

;
; Each cell of a 4 x 4 grid is represented by a power of 2:
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
; It outputs the board both as a text (see below) and graphically (in VGA mode).
;
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
;

main proc
    call parseNumber                ; parse arg and put it in [81h]
    call printBoard                 ; print the board in text form
    call waitForKey

    ; vga mode
    mov ax,13h                      ; 320x200 256col mode
    int 10h

    call drawBoard
    ; todo draw grid lines
    call drawGrid
    call waitForKey

    ; restore text mode
    mov ax,3                        ; 80x25 text mode
    int 10h

    ; exit
    mov ah,4Ch
    int 21h
main endp

; waits for a key press
waitForKey proc
    xor ax,ax
    int 16h
    ret
waitForKey endp

; prints cell with index (0 to 15) in ax (n)
; each cell is 50x50 pixel and there is a 60 pixel margin on both the left and ride side of the board
printField proc
    push ax
    push bx
    push cx
    push dx
    mov bl,4                        ; bl = 4
    xor bh,bh
    div bl                          ; ah = n % 4, al = n / 4
    mov bh,ah                       ; bh <- ah
    push ax
    mov ax,50
    mul bh                          ; ax = 50 * (n % 4)
    add ax,60                       ; ax = 60 + 50 * (n % 4)
    mov cx,ax
    pop bx                          ; bh = n % 4, bl = n / 4
    mov ax,50
    mul bl                          ; ax = 50 * (n / 4)
    push cx
    push ax
    pop ax                          ; ax = y
    mov bx,320
    mul bx                          ; ax = 320 * y
    pop bx                          ; bx = x
    add ax,bx                       ; ax = x + 320 * y
    mov si,ax
    mov ax,0A000h                   ; ax = 0A000h + x + 320 * y = memory address for pixel (x,y)
    push ax
    pop es
    mov dx,0
outerLoop:
    cmp dx,50
    je outerDone
    mov cx,0
innerLoop:
    cmp cx,50
    je innerDone
    mov byte ptr es:[si],blue       ; write a blue pixel
    inc si
    inc cx
    jmp innerLoop
innerDone:
    add si,270
    inc dx
    jmp outerLoop
outerDone:
    pop dx
    pop cx
    pop bx
    pop ax
    ret
printField endp

; reads a number from the (only) command argument and writes its value back to [81h]
parseNumber proc
    push ax
    push bx
    push cx
    push dx
    push si
    xor bx,bx
    mov bl,byte ptr es:[80h]        ; bl = length of command arguments
    cmp bl,6                        ; leading whitespace + 5 digits for largest possible 16-bit value
    jg printError
    dec bx
    mov cx,bx                       ; cl = number of characters
    mov si,81h
    xor ch,ch
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
    xor ah,ah
    mul bp                          ; ax *= bp
    add bx,ax
    jc printError                   ; input > 65535 results in a carry -> error
    mov ax,bp
    mov bp,10
    mul bp                          ; ax *= bp
    mov bp,ax
    dec si
    loop repeat
    mov si,81h
    mov [si],bx                     ; write the board representation to memory
    pop si
    pop dx
    pop cx
    pop bx
    pop ax
    ret
printError:
    mov ax,@data
    mov ds,ax
    mov ah,09h                      ; print string
    lea dx,errorMessage
    int 21h
    pop si
    pop dx
    pop cx
    pop bx
    mov ah,4ch                      ; exit
    mov al,1                        ; exit code 1
    int 21h
parseNumber endp

; prints the board
printBoard proc
    push ax
    push bx
    push cx
    push dx
    push es
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
    pop es
    pop dx
    pop cx
    pop bx
    pop ax
    ret
printBoard endp

; draws the board
drawBoard proc
    push ax
    push bx
    push cx
    push dx
    push es
    mov cx,0                        ; loop counter
drawLoop:
    cmp cx, 16
    je drawExit
    mov ax,1
    mov dx,0
drawShift:
    cmp dx,cx                       ; if dx >= cx
    jge drawNext
    shl ax,1                        ; ax << 1
    inc dx                          ; dx++
    jmp drawShift
drawNext:
    push es
    mov bx,word ptr es:[81h]        ; load board from [81h]
    and bx,ax                       ; bx &= ax
    cmp bx,0                        ; if ax != 0
    jne drawFilled
    jmp drawDone
drawFilled:
    mov ax,cx
    call printField
drawDone:
    pop es
    inc cx                          ; cx++
    jmp drawLoop
drawExit:
    pop es
    pop dx
    pop cx
    pop bx
    pop ax
    ret
drawBoard endp

; draws the grid
; todo: draw horizontal and vertical lines in a single pass
drawGrid proc
    push ax
    push bx
    push cx
    push dx
    push es
    push si
    call drawHorizontal
    call drawVertical
    pop si
    pop es
    pop dx
    pop cx
    pop bx
    pop ax
    ret
drawGrid endp

drawHorizontal proc
    mov bx, 0A000h
    push bx
    pop es
    mov si,60
    mov cx,0
loop4:
    cmp cx,4
    je end4
    mov dx,0
loop3:
    cmp dx,200
    je end3
    mov byte ptr es:[si],grid
    inc si
    inc dx
    jmp loop3
end3:
    add si,15800
    inc cx
    jmp loop4
end4:
    mov si,63740
    mov cx,0
loop5:
    cmp cx,200
    je end5
    mov byte ptr es:[si],grid
    inc si
    inc cx
    jmp loop5
end5:
    ret
drawHorizontal endp

drawVertical proc
    mov bx,0A000h
    push bx
    pop es
    mov si,60
    mov cx,0
loop2:
    cmp cx,200
    je end2
    mov dx,0
loop1:
    cmp dx,4
    jg end1
    mov byte ptr es:[si],grid
    add si,50
    inc dx
    jmp loop1
end1:
    add si,70
    inc cx
    jmp loop2
end2:
    ret
drawVertical endp
end main