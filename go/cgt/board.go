package cgt

func NewBoard(h int, w int) Board {
	board := make([][]bool, h)
	for i := 0; i < h; i++ {
		board[i] = make([]bool, w)
	}
	return board
}

func newBoard(b Board) Board {
	b1 := make([][]bool, len(b))
	for i := range b {
		b1[i] = make([]bool, len(b[i]))
		copy(b1[i], b[i])
	}
	return b1
}

func board2bin(b Board) uint64 {
	var bin uint64
	w := len(b)
	h := len(b[0])
	for i := 0; i < w*h; i++ {
		if b[i%w][i/w] {
			bin += (1 << i)
		}
	}
	return bin
}