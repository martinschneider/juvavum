const JUV = 0;
const DJUV = 1;
const CRAM = 2;
const DOM = 3;

// number of free fields that triggers exhaustive endgame analysis or min-max search
// if chosen too high the browser might become unresponsive
// TODO: these can be increased once we consider symmetries
const ENDGAME_LIMITS = [12, 20, 20, 30]; // index 0: JUV, index 1: DJUV etc.
const MIN_MAX_DEPTH = 3;

const REST_URL = "http://localhost:8080"
const NETWORK_TIMEOUT = 500 // milliseconds
let best = null;

// Game properties
let properties = new Map();
let gameOver = false;
let moves = 0;

// Board states
let board;
let prevBoard;

// Data for game analysis
let gValues = new Map();
let successorsMap = new Map();

// Music and sound effects
let bgMusic = new Audio("audio/mozart.mp3");
let placePiece = new Audio("audio/domino1.m4a");
let moveSound = new Audio("audio/domino2.m4a");
let takeBackPiece = new Audio("audio/pickup.mp3");
let clearBoardPiece = new Audio("audio/sweep.mp3");

// Translations
I18n = {};
I18n.defaultLocale = "en";

const timeout = (prom, time, exception) => {
	let timer;
	return Promise.race([
		prom,
		new Promise((_r, rej) => timer = setTimeout(rej, time, exception))
	]).finally(() => clearTimeout(timer));
}