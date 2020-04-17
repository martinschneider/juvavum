package main

import "fmt"
import "flag"
import "time"
import "github.com/hako/durafmt"
import "board"

type Board [][]bool

func main() {
	h := flag.Int("h", 3, "height")
	w := flag.Int("w", 3, "width")
	m := flag.Bool("m", false, "misere")
	flag.Parse()
	var misere string
	if *m==true{
		misere = ", misere"
	}else{
		misere = ""
	}
	fmt.Printf("CRAM[%dx%d%s]\n", *h, *w, misere)
	start := time.Now()
	g, gMap := board.Grundy(board.NewBoard(*h,*w),*m)
	elapsed := time.Since(start)
	duration := durafmt.Parse(elapsed)
	var winner string
	if g==0{
		winner = "second"
	}else{
		winner = "first"
	}
	fmt.Printf("The g-value of the starting position is %d. The %s player can always win.\n", g, winner)
	fmt.Printf("Number of positions: %d\n", len(gMap))
	fmt.Printf("Duration: %s\n", duration)
}