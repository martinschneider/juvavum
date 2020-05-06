package main

import (
	"testing"
)

func TestCram(t *testing.T) {
	cramTests := []struct {
		h    int
		w    int
		g    int
		m    bool
		pos  int
		game Game
	}{
		{
			h:    2,
			w:    2,
			g:    0,
			pos:  6,
			game: CRAM,
		},
		{
			h:    2,
			w:    3,
			g:    1,
			pos:  18,
			game: CRAM,
		},
		{
			h:    2,
			w:    4,
			g:    0,
			pos:  54,
			game: CRAM,
		},
		{
			h:    3,
			w:    3,
			g:    0,
			pos:  98,
			game: CRAM,
		},
		{
			h:    3,
			w:    4,
			g:    1,
			pos:  550,
			game: CRAM,
		},
		{
			h:    3,
			w:    5,
			g:    1,
			pos:  3054,
			game: CRAM,
		},
		{
			h:    4,
			w:    4,
			g:    0,
			pos:  5700,
			game: CRAM,
		},
		// misere
		{
			h:    2,
			w:    2,
			g:    1,
			pos:  6,
			m:    true,
			game: CRAM,
		},
		{
			h:    2,
			w:    3,
			g:    0,
			pos:  18,
			m:    true,
			game: CRAM,
		},
		{
			h:    2,
			w:    4,
			g:    1,
			pos:  54,
			m:    true,
			game: CRAM,
		},
		{
			h:    3,
			w:    3,
			g:    1,
			pos:  98,
			m:    true,
			game: CRAM,
		},
		{
			h:    3,
			w:    4,
			g:    0,
			pos:  550,
			m:    true,
			game: CRAM,
		},
		{
			h:    3,
			w:    5,
			g:    0,
			pos:  3054,
			m:    true,
			game: CRAM,
		},
		{
			h:    4,
			w:    4,
			g:    0,
			pos:  5700,
			m:    true,
			game: CRAM,
		},
		// Domino Juvavum
		{
			h:    2,
			w:    2,
			g:    0,
			pos:  6,
			game: DJUV,
		},
		{
			h:    2,
			w:    3,
			g:    1,
			pos:  18,
			game: DJUV,
		},
		{
			h:    2,
			w:    4,
			g:    0,
			pos:  54,
			game: DJUV,
		},
		{
			h:    3,
			w:    3,
			g:    0,
			pos:  98,
			game: DJUV,
		},
		{
			h:    3,
			w:    4,
			g:    1,
			pos:  550,
			game: DJUV,
		},
		{
			h:    3,
			w:    5,
			g:    3,
			pos:  3054,
			game: DJUV,
		},
		{
			h:    4,
			w:    4,
			g:    0,
			pos:  5700,
			game: DJUV,
		},
		// misere
		{
			h:    2,
			w:    2,
			g:    1,
			pos:  6,
			m:    true,
			game: DJUV,
		},
		{
			h:    2,
			w:    3,
			g:    0,
			pos:  18,
			m:    true,
			game: DJUV,
		},
		{
			h:    2,
			w:    4,
			g:    1,
			pos:  54,
			m:    true,
			game: DJUV,
		},
		{
			h:    3,
			w:    3,
			g:    1,
			pos:  98,
			m:    true,
			game: DJUV,
		},
		{
			h:    3,
			w:    4,
			g:    2,
			pos:  550,
			m:    true,
			game: DJUV,
		},
		{
			h:    3,
			w:    5,
			g:    3,
			pos:  3054,
			m:    true,
			game: DJUV,
		},
		{
			h:    4,
			w:    4,
			g:    1,
			pos:  5700,
			m:    true,
			game: DJUV,
		},
	}

	var analysis CRAMAnalysis
	for _, tt := range cramTests {
		analysis.g = tt.game
		g, gMap := Grundy(NewBoard(tt.h, tt.w), analysis, tt.m)
		Clear()
		misere := ""
		if tt.m {
			misere = "M"
		}
		if g != tt.g {
			t.Errorf("g(%s%s(%d,%d)) = %d; want %d", analysis.g, misere, tt.h, tt.w, g, tt.g)
		}
		var pos = len(gMap)
		if pos != tt.pos {
			t.Errorf("pos(%s%s(%d,%d)) = %d; want %d", analysis.g, misere, tt.h, tt.w, pos, tt.pos)
		}
	}
}

func BenchmarkCram2x2(b *testing.B) {
	var analysis CRAMAnalysis
	analysis.g = CRAM
	for i := 0; i < b.N; i++ {
		Grundy(NewBoard(2, 2), analysis, false)
		Clear()
	}
}

func BenchmarkCram3x3(b *testing.B) {
	var analysis CRAMAnalysis
	analysis.g = CRAM
	for i := 0; i < b.N; i++ {
		Grundy(NewBoard(3, 3), analysis, false)
		Clear()
	}
}

func BenchmarkCram4x4(b *testing.B) {
	var analysis CRAMAnalysis
	analysis.g = CRAM
	for i := 0; i < b.N; i++ {
		Grundy(NewBoard(4, 4), analysis, false)
		Clear()
	}
}
