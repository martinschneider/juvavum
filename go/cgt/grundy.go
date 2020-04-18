package cgt

var grundyValues grundyMap = make(grundyMap)

func Grundy(b Board, game Game, misere bool) (int, grundyMap) {
	bin := board2bin(b)
	var g int
	if g, ok := grundyValues[bin]; ok {
		return g, grundyValues
	}
	var children = make(BoardMap)
	GetSuccessors(b, game, children)
	if len(children) == 0 {
		if misere == true {
			g = 1
		} else {
			g = 0
		}
		grundyValues[bin] = g
		return g, grundyValues
	}
	g = mex(children, game, misere)
	grundyValues[bin] = g
	return g, grundyValues
}

func mex(children BoardMap, g Game, misere bool) int {
	i := 0
	mex := -1
	for mex == -1 {
		found := false
		for _, b := range children {
			j, _ := Grundy(b, g, misere)
			if j == i {
				found = true
				break
			}
		}
		if found == false {
			mex = i
		}
		i++
	}
	return mex
}
