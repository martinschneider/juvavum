package board

import "fmt"

type Board [][]bool

var grundyMap map[uint64]int = make(map[uint64]int)

func NewBoard(h int, w int) Board {
	board := make([][]bool, h)
	for i := 0; i < h; i++ {
		board[i] = make([]bool, w)
	}
	return board
}

func Grundy(b Board, misere bool) (int, map[uint64]int){
	bin := board2bin(b)
	var g int
	if g, ok := grundyMap[bin]; ok{
		return g, grundyMap
	}
	var children = make(map[uint64]Board)
	addChildren(b, children)
	if (len(children)==0){
		if misere == true{
			g=1
		} else {
			g=0
		}
		grundyMap[bin]=g
		return g, grundyMap
	}
	g=mex(children, misere);
    grundyMap[bin]=g
	return g, grundyMap
}

func mex(children map[uint64]Board, misere bool) int {
    i := 0;
    mex := -1;
    for ; mex == -1 ; {
      found := false
      for _, b := range children{
        j, _ := Grundy(b, misere)
        if j == i {
          found = true
          break
        }
      }
      if found==false {
        mex = i
      }
      i++
    }
    return mex
  }

func addChildren(b Board, children map[uint64]Board){
	for k, v := range GetSuccessors(b, children) {
    	children[k] = v
	}
}


func GetSuccessors(b Board, children map[uint64]Board) map[uint64]Board{
	children = getColumnSuccessors(b, children)
	children = getRowSuccessors(b, children)
	return children
}

func getColumnSuccessors(b Board, children map[uint64]Board) map[uint64]Board{
	for j := 1; j <= len(b[0]); j++ {
      	for i := 1; i < len(b); i++ {
      		if !b[i-1][j-1] && !b[i][j-1] {
      			b1 := newBoard(b)
      			b1[i-1][j-1]=true
      			b1[i][j-1]=true
      			children[board2bin(b1)]=b1
      		}
    	}
    }
	return children
}

func getRowSuccessors(b Board, children map[uint64]Board) map[uint64]Board{
	for i := 1; i <= len(b); i++ {
      	for j := 1; j < len(b[0]); j++ {
      		if !b[i-1][j-1] && !b[i-1][j] {
      			b1 := newBoard(b)
      			b1[i-1][j-1]=true
      			b1[i-1][j]=true
      			children[board2bin(b1)]=b1
      		}
    	}
    }
	return children
}

func newBoard(b Board) Board {
	b1 := make([][]bool, len(b))
	for i := range b {
    	b1[i] = make([]bool, len(b[i]))
    	copy(b1[i], b[i])
	}
	return b1
}

// unused
func bin2board(h int, w int, bin uint64) Board {
	var b = NewBoard(h, w)
	for i := 0; i < h*w; i++ {
		b[i/w][i%w] = (bin & (1 << i)) != 0
	}
	return b
}

func board2bin(b Board) uint64 {
	var bin uint64
    w := len(b)
    h := len(b[0])
    for i := 0; i < w * h; i++ {
      if (b[i % w][i / w]) {
        bin += (1 << i);
      }
    }
    return bin;
}

// unused
func Print(board Board) {
	for _, r := range board {
		for _, c := range r {
			if c == true {
				fmt.Print("X ")
			} else {
				fmt.Print("- ")
			}
		}
		fmt.Print("\n")
	}
	fmt.Print("\n")
}
