package main

type Game int

const (
	CRAM Game = iota
	DJUV
)

func (g Game) String() string {
	return [...]string{"CRAM", "DJUV"}[g]
}

type CRAMAnalysis struct {
	g Game
}

func (analysis CRAMAnalysis) Successors(b Board, children BoardMap) BoardMap {
	children = getColumnSuccessors(b, analysis.g, children)
	children = getRowSuccessors(b, analysis.g, children)
	return children
}

func getColumnSuccessors(b Board, g Game, children BoardMap) BoardMap {
	for j := 1; j <= len(b[0]); j++ {
		getColumnSuccessorsInCol(b, g, j, children)
	}
	return children
}

func getColumnSuccessorsInCol(b Board, g Game, j int, children BoardMap) BoardMap {
	for i := 1; i < len(b); i++ {
		if !b[i-1][j-1] && !b[i][j-1] {
			b1 := newBoard(b)
			b1[i-1][j-1] = true
			b1[i][j-1] = true
			children[board2bin(b1)] = b1
			if g == DJUV {
				for k, v := range getColumnSuccessorsInCol(b1, g, j, make(BoardMap)) {
					children[k] = v
				}
			}
		}
	}
	return children
}

func getRowSuccessors(b Board, g Game, children BoardMap) BoardMap {
	for i := 1; i <= len(b); i++ {
		getRowSuccessorsInRow(b, g, i, children)
	}
	return children
}

func getRowSuccessorsInRow(b Board, g Game, i int, children BoardMap) BoardMap {
	for j := 1; j < len(b[0]); j++ {
		if !b[i-1][j-1] && !b[i-1][j] {
			b1 := newBoard(b)
			b1[i-1][j-1] = true
			b1[i-1][j] = true
			children[board2bin(b1)] = b1
			if g == DJUV {
				for k, v := range getRowSuccessorsInRow(b1, g, i, make(BoardMap)) {
					children[k] = v
				}
			}
		}
	}
	return children
}
