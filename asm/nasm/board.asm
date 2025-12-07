; Display Juvavum boards - NASM version
; Usage: ./board <width> <height> <value>

section .data
    ; Cell characters
    filled_cell:    db "██", 0
    empty_cell:     db "  ", 0
    newline:        db 10, 0

    ; Box-drawing characters (UTF-8)
    corner_tl:      db "┌", 0
    corner_tr:      db "┐", 10, 0
    corner_bl:      db "└", 0
    corner_br:      db "┘", 10, 0
    t_down:         db "┬", 0
    t_up:           db "┴", 0
    t_right:        db "├", 0
    t_left:         db "┤", 10, 0
    cross:          db "┼", 0
    h_line:         db "──", 0
    v_line:         db "│", 0

    ; Minimal error message
    error_msg:      db "Usage: board W H VALUE (W,H:1-8, max 64 cells)", 10
    error_len:      equ $ - error_msg

section .bss
    width:          resq 1
    height:         resq 1
    board_value:    resq 1
    cell_count:     resq 1

section .text
    global _start

_start:
    pop rdi
    cmp rdi, 4
    jne print_error

    pop rsi
    pop rsi
    call parse_number
    mov [width], rax
    cmp rax, 1
    jl print_error
    cmp rax, 8
    jg print_error

    pop rsi
    call parse_number
    mov [height], rax
    cmp rax, 1
    jl print_error
    cmp rax, 8
    jg print_error

    mov rax, [width]
    mul qword [height]
    cmp rax, 64
    jg print_error
    mov [cell_count], rax

    pop rsi
    call parse_number
    mov [board_value], rax

    call draw_top_border
    xor r12, r12
    xor r13, r13

draw_row_start:
    cmp r13, [height]
    jge draw_bottom_border

    mov rsi, v_line
    call print_string

    xor r14, r14

draw_row_loop:
    cmp r14, [width]
    jge draw_row_end

    mov rdi, r12
    call test_bit
    mov rsi, empty_cell
    test rax, rax
    jz .empty
    mov rsi, filled_cell
.empty:
    call print_string

    inc r14
    cmp r14, [width]
    jge draw_row_end

    mov rsi, v_line
    call print_string

    inc r12
    jmp draw_row_loop

draw_row_end:
    mov rsi, v_line
    call print_string
    mov rsi, newline
    call print_string

    inc r12
    inc r13

    cmp r13, [height]
    jge draw_row_start

    call draw_h_separator
    jmp draw_row_start

draw_bottom_border:
    call draw_bottom_border_line
    mov rax, 60
    xor rdi, rdi
    syscall

print_error:
    mov rax, 1
    mov rdi, 1
    mov rsi, error_msg
    mov rdx, error_len
    syscall
    mov rax, 60
    mov rdi, 1
    syscall

parse_number:
    push rbx
    push rcx
    push rdx
    xor rax, rax
.loop:
    movzx rcx, byte [rsi]
    sub rcx, '0'
    cmp rcx, 9
    ja .done
    imul rax, 10
    add rax, rcx
    inc rsi
    jmp .loop
.done:
    pop rdx
    pop rcx
    pop rbx
    ret

print_string:
    push rax
    push rdi
    push rdx
    push rcx
    push rsi
    mov rdi, rsi
    xor rcx, rcx
.find:
    cmp byte [rdi], 0
    je .found
    inc rdi
    inc rcx
    jmp .find
.found:
    mov rax, 1
    mov rdi, 1
    mov rdx, rcx
    syscall
    pop rsi
    pop rcx
    pop rdx
    pop rdi
    pop rax
    ret

test_bit:
    push rcx
    mov rax, [board_value]
    mov rcx, rdi
    shr rax, cl
    and rax, 1
    pop rcx
    ret

draw_top_border:
    push rcx
    mov rsi, corner_tl
    call print_string
    mov rcx, [width]
.loop:
    mov rsi, h_line
    call print_string
    dec rcx
    jz .done
    mov rsi, t_down
    call print_string
    jmp .loop
.done:
    mov rsi, corner_tr
    call print_string
    pop rcx
    ret

draw_h_separator:
    push rcx
    mov rsi, t_right
    call print_string
    mov rcx, [width]
.loop:
    mov rsi, h_line
    call print_string
    dec rcx
    jz .done
    mov rsi, cross
    call print_string
    jmp .loop
.done:
    mov rsi, t_left
    call print_string
    pop rcx
    ret

draw_bottom_border_line:
    push rcx
    mov rsi, corner_bl
    call print_string
    mov rcx, [width]
.loop:
    mov rsi, h_line
    call print_string
    dec rcx
    jz .done
    mov rsi, t_up
    call print_string
    jmp .loop
.done:
    mov rsi, corner_br
    call print_string
    pop rcx
    ret
