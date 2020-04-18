package main

import "fmt"
import "flag"
import "time"
import "cgt"
import "github.com/hako/durafmt"

func main() {
	// settings
	h := flag.Int("h", 3, "height of the game board")
	w := flag.Int("w", 3, "width of the game board")
	m := flag.Bool("m", false, "misère")
	g := flag.String("g", "CRAM", "game type")
	flag.Parse()

	var game cgt.Game
	switch *g {
		case "CRAM":
    		game = cgt.CRAM
		case "DJUV":
    		game = cgt.DJUV
		default:
    		game = cgt.CRAM
	}
	if *h > *w{
		h, w = w, h
	}

	// print header line
	var misere string
	if *m == true {
		misere = ", misere"
	} else {
		misere = ""
	}
	fmt.Printf("\n%s[%dx%d%s]\n", game, *h, *w, misere)

	// calculation
	start := time.Now()
	grundy, gMap := cgt.Grundy(cgt.NewBoard(*h, *w), game, *m)
	elapsed := time.Since(start)

	// print results
	duration := durafmt.Parse(elapsed)
	var winner string
	if grundy == 0 {
		winner = "second"
	} else {
		winner = "first"
	}
	fmt.Printf("The g-value of the starting position is %d. The %s player can always win.\n", grundy, winner)
	fmt.Printf("Number of positions: %d\n", len(gMap))
	fmt.Printf("Duration: %s\n", duration)
}
