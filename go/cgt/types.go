package cgt

type Game int
const (
    CRAM Game = iota
    DJUV
)
func (g Game) String() string {
    return [...]string{"CRAM", "DJUV"}[g]
}
type Board [][]bool
type BoardMap map[uint64]Board
type grundyMap map[uint64]int
type succFunc func(b Board, g Game, children BoardMap) BoardMap