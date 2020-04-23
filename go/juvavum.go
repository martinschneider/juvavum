package main

import (
	"flag"
	"fmt"
	"github.com/boltdb/bolt"
	"github.com/briandowns/spinner"
	"github.com/hako/durafmt"
	"strconv"
	"time"
)

type Analysis interface {
	Successors(b Board, children BoardMap) BoardMap
}

func main() {
	// settings
	h := flag.Int("h", 3, "height of the game board")
	w := flag.Int("w", 3, "width of the game board")
	m := flag.Bool("m", false, "misère")
	g := flag.String("g", "CRAM", "game type")
	db := flag.String("db", "", "file to store the results")
	flag.Parse()

	var game Game
	var analysis CRAMAnalysis
	switch *g {
	case "CRAM":
		game = CRAM
		analysis.g = CRAM
	case "DJUV":
		game = DJUV
		analysis.g = DJUV
	default:
		game = CRAM
	}

	// print header line
	var misere string
	if *m == true {
		misere = ", misere"
	} else {
		misere = ""
	}
	var analysisName string = fmt.Sprintf("%s[%dx%d%s]", game, *h, *w, misere)
	fmt.Printf("\n%s\n", analysisName)

	// calculation
	start := time.Now()
	grundy, gMap := Grundy(NewBoard(*h, *w), analysis, *m)
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

	// store in bolt db
	if *db != "" {
		fmt.Printf("\nWriting results to %s\n", *db)
		s := spinner.New(spinner.CharSets[9], 100*time.Millisecond)
		s.FinalMSG = "Done\n"
		s.Start()
		save2DB(*db, analysisName, gMap)
		s.Stop()
	}
}

func save2DB(dbName string, bucket string, gMap grundyMap) {
	db, err := bolt.Open(dbName, 0600, nil)
	defer db.Close()
	if err != nil {
		//log.Fatal(err)
	}
	db.Update(func(tx *bolt.Tx) error {
		b, err := tx.CreateBucketIfNotExists([]byte(bucket))
		if err != nil {
			return fmt.Errorf("create bucket: %s", err)
		}
		for k, v := range gMap {
			b.Put([]byte(strconv.FormatUint(k, 10)), []byte(strconv.Itoa(v)))
		}
		return nil
	})
}
