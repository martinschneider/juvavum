use rustc_hash::FxHashMap;
use std::env;
use std::time::Instant;

const H_DOMINO: u64 = 3;

// Lookup tables for efficient bit operations
// FLIPLR[width] maps row values to their horizontally mirrored versions
const FLIPLR: &[&[u64]] = &[
    &[],                                                     // width 0 (unused)
    &[0, 1],                                                 // width 1
    &[0, 2, 1, 3],                                           // width 2
    &[0, 4, 2, 6, 1, 5, 3, 7],                               // width 3
    &[0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15], // width 4
    &[
        0, 16, 8, 24, 4, 20, 12, 28, 2, 18, 10, 26, 6, 22, 14, 30, 1, 17, 9, 25, 5, 21, 13, 29, 3,
        19, 11, 27, 7, 23, 15, 31,
    ], // width 5
    &[
        0, 32, 16, 48, 8, 40, 24, 56, 4, 36, 20, 52, 12, 44, 28, 60, 2, 34, 18, 50, 10, 42, 26, 58,
        6, 38, 22, 54, 14, 46, 30, 62, 1, 33, 17, 49, 9, 41, 25, 57, 5, 37, 21, 53, 13, 45, 29, 61,
        3, 35, 19, 51, 11, 43, 27, 59, 7, 39, 23, 55, 15, 47, 31, 63,
    ], // width 6
    &[
        0, 64, 32, 96, 16, 80, 48, 112, 8, 72, 40, 104, 24, 88, 56, 120, 4, 68, 36, 100, 20, 84,
        52, 116, 12, 76, 44, 108, 28, 92, 60, 124, 2, 66, 34, 98, 18, 82, 50, 114, 10, 74, 42, 106,
        26, 90, 58, 122, 6, 70, 38, 102, 22, 86, 54, 118, 14, 78, 46, 110, 30, 94, 62, 126, 1, 65,
        33, 97, 17, 81, 49, 113, 9, 73, 41, 105, 25, 89, 57, 121, 5, 69, 37, 101, 21, 85, 53, 117,
        13, 77, 45, 109, 29, 93, 61, 125, 3, 67, 35, 99, 19, 83, 51, 115, 11, 75, 43, 107, 27, 91,
        59, 123, 7, 71, 39, 103, 23, 87, 55, 119, 15, 79, 47, 111, 31, 95, 63, 127,
    ], // width 7
    &[
        0, 128, 64, 192, 32, 160, 96, 224, 16, 144, 80, 208, 48, 176, 112, 240, 8, 136, 72, 200,
        40, 168, 104, 232, 24, 152, 88, 216, 56, 184, 120, 248, 4, 132, 68, 196, 36, 164, 100, 228,
        20, 148, 84, 212, 52, 180, 116, 244, 12, 140, 76, 204, 44, 172, 108, 236, 28, 156, 92, 220,
        60, 188, 124, 252, 2, 130, 66, 194, 34, 162, 98, 226, 18, 146, 82, 210, 50, 178, 114, 242,
        10, 138, 74, 202, 42, 170, 106, 234, 26, 154, 90, 218, 58, 186, 122, 250, 6, 134, 70, 198,
        38, 166, 102, 230, 22, 150, 86, 214, 54, 182, 118, 246, 14, 142, 78, 206, 46, 174, 110,
        238, 30, 158, 94, 222, 62, 190, 126, 254, 1, 129, 65, 193, 33, 161, 97, 225, 17, 145, 81,
        209, 49, 177, 113, 241, 9, 137, 73, 201, 41, 169, 105, 233, 25, 153, 89, 217, 57, 185, 121,
        249, 5, 133, 69, 197, 37, 165, 101, 229, 21, 149, 85, 213, 53, 181, 117, 245, 13, 141, 77,
        205, 45, 173, 109, 237, 29, 157, 93, 221, 61, 189, 125, 253, 3, 131, 67, 195, 35, 163, 99,
        227, 19, 147, 83, 211, 51, 179, 115, 243, 11, 139, 75, 203, 43, 171, 107, 235, 27, 155, 91,
        219, 59, 187, 123, 251, 7, 135, 71, 199, 39, 167, 103, 231, 23, 151, 87, 215, 55, 183, 119,
        247, 15, 143, 79, 207, 47, 175, 111, 239, 31, 159, 95, 223, 63, 191, 127, 255,
    ], // width 8
    &[
        0, 256, 128, 384, 64, 320, 192, 448, 32, 288, 160, 416, 96, 352, 224, 480, 16, 272, 144,
        400, 80, 336, 208, 464, 48, 304, 176, 432, 112, 368, 240, 496, 8, 264, 136, 392, 72, 328,
        200, 456, 40, 296, 168, 424, 104, 360, 232, 488, 24, 280, 152, 408, 88, 344, 216, 472, 56,
        312, 184, 440, 120, 376, 248, 504, 4, 260, 132, 388, 68, 324, 196, 452, 36, 292, 164, 420,
        100, 356, 228, 484, 20, 276, 148, 404, 84, 340, 212, 468, 52, 308, 180, 436, 116, 372, 244,
        500, 12, 268, 140, 396, 76, 332, 204, 460, 44, 300, 172, 428, 108, 364, 236, 492, 28, 284,
        156, 412, 92, 348, 220, 476, 60, 316, 188, 444, 124, 380, 252, 508, 2, 258, 130, 386, 66,
        322, 194, 450, 34, 290, 162, 418, 98, 354, 226, 482, 18, 274, 146, 402, 82, 338, 210, 466,
        50, 306, 178, 434, 114, 370, 242, 498, 10, 266, 138, 394, 74, 330, 202, 458, 42, 298, 170,
        426, 106, 362, 234, 490, 26, 282, 154, 410, 90, 346, 218, 474, 58, 314, 186, 442, 122, 378,
        250, 506, 6, 262, 134, 390, 70, 326, 198, 454, 38, 294, 166, 422, 102, 358, 230, 486, 22,
        278, 150, 406, 86, 342, 214, 470, 54, 310, 182, 438, 118, 374, 246, 502, 14, 270, 142, 398,
        78, 334, 206, 462, 46, 302, 174, 430, 110, 366, 238, 494, 30, 286, 158, 414, 94, 350, 222,
        478, 62, 318, 190, 446, 126, 382, 254, 510, 1, 257, 129, 385, 65, 321, 193, 449, 33, 289,
        161, 417, 97, 353, 225, 481, 17, 273, 145, 401, 81, 337, 209, 465, 49, 305, 177, 433, 113,
        369, 241, 497, 9, 265, 137, 393, 73, 329, 201, 457, 41, 297, 169, 425, 105, 361, 233, 489,
        25, 281, 153, 409, 89, 345, 217, 473, 57, 313, 185, 441, 121, 377, 249, 505, 5, 261, 133,
        389, 69, 325, 197, 453, 37, 293, 165, 421, 101, 357, 229, 485, 21, 277, 149, 405, 85, 341,
        213, 469, 53, 309, 181, 437, 117, 373, 245, 501, 13, 269, 141, 397, 77, 333, 205, 461, 45,
        301, 173, 429, 109, 365, 237, 493, 29, 285, 157, 413, 93, 349, 221, 477, 61, 317, 189, 445,
        125, 381, 253, 509, 3, 259, 131, 387, 67, 323, 195, 451, 35, 291, 163, 419, 99, 355, 227,
        483, 19, 275, 147, 403, 83, 339, 211, 467, 51, 307, 179, 435, 115, 371, 243, 499, 11, 267,
        139, 395, 75, 331, 203, 459, 43, 299, 171, 427, 107, 363, 235, 491, 27, 283, 155, 411, 91,
        347, 219, 475, 59, 315, 187, 443, 123, 379, 251, 507, 7, 263, 135, 391, 71, 327, 199, 455,
        39, 295, 167, 423, 103, 359, 231, 487, 23, 279, 151, 407, 87, 343, 215, 471, 55, 311, 183,
        439, 119, 375, 247, 503, 15, 271, 143, 399, 79, 335, 207, 463, 47, 303, 175, 431, 111, 367,
        239, 495, 31, 287, 159, 415, 95, 351, 223, 479, 63, 319, 191, 447, 127, 383, 255, 511,
    ], // width 9
    &[
        0, 512, 256, 768, 128, 640, 384, 896, 64, 576, 320, 832, 192, 704, 448, 960, 32, 544, 288,
        800, 160, 672, 416, 928, 96, 608, 352, 864, 224, 736, 480, 992, 16, 528, 272, 784, 144,
        656, 400, 912, 80, 592, 336, 848, 208, 720, 464, 976, 48, 560, 304, 816, 176, 688, 432,
        944, 112, 624, 368, 880, 240, 752, 496, 1008, 8, 520, 264, 776, 136, 648, 392, 904, 72,
        584, 328, 840, 200, 712, 456, 968, 40, 552, 296, 808, 168, 680, 424, 936, 104, 616, 360,
        872, 232, 744, 488, 1000, 24, 536, 280, 792, 152, 664, 408, 920, 88, 600, 344, 856, 216,
        728, 472, 984, 56, 568, 312, 824, 184, 696, 440, 952, 120, 632, 376, 888, 248, 760, 504,
        1016, 4, 516, 260, 772, 132, 644, 388, 900, 68, 580, 324, 836, 196, 708, 452, 964, 36, 548,
        292, 804, 164, 676, 420, 932, 100, 612, 356, 868, 228, 740, 484, 996, 20, 532, 276, 788,
        148, 660, 404, 916, 84, 596, 340, 852, 212, 724, 468, 980, 52, 564, 308, 820, 180, 692,
        436, 948, 116, 628, 372, 884, 244, 756, 500, 1012, 12, 524, 268, 780, 140, 652, 396, 908,
        76, 588, 332, 844, 204, 716, 460, 972, 44, 556, 300, 812, 172, 684, 428, 940, 108, 620,
        364, 876, 236, 748, 492, 1004, 28, 540, 284, 796, 156, 668, 412, 924, 92, 604, 348, 860,
        220, 732, 476, 988, 60, 572, 316, 828, 188, 700, 444, 956, 124, 636, 380, 892, 252, 764,
        508, 1020, 2, 514, 258, 770, 130, 642, 386, 898, 66, 578, 322, 834, 194, 706, 450, 962, 34,
        546, 290, 802, 162, 674, 418, 930, 98, 610, 354, 866, 226, 738, 482, 994, 18, 530, 274,
        786, 146, 658, 402, 914, 82, 594, 338, 850, 210, 722, 466, 978, 50, 562, 306, 818, 178,
        690, 434, 946, 114, 626, 370, 882, 242, 754, 498, 1010, 10, 522, 266, 778, 138, 650, 394,
        906, 74, 586, 330, 842, 202, 714, 458, 970, 42, 554, 298, 810, 170, 682, 426, 938, 106,
        618, 362, 874, 234, 746, 490, 1002, 26, 538, 282, 794, 154, 666, 410, 922, 90, 602, 346,
        858, 218, 730, 474, 986, 58, 570, 314, 826, 186, 698, 442, 954, 122, 634, 378, 890, 250,
        762, 506, 1018, 6, 518, 262, 774, 134, 646, 390, 902, 70, 582, 326, 838, 198, 710, 454,
        966, 38, 550, 294, 806, 166, 678, 422, 934, 102, 614, 358, 870, 230, 742, 486, 998, 22,
        534, 278, 790, 150, 662, 406, 918, 86, 598, 342, 854, 214, 726, 470, 982, 54, 566, 310,
        822, 182, 694, 438, 950, 118, 630, 374, 886, 246, 758, 502, 1014, 14, 526, 270, 782, 142,
        654, 398, 910, 78, 590, 334, 846, 206, 718, 462, 974, 46, 558, 302, 814, 174, 686, 430,
        942, 110, 622, 366, 878, 238, 750, 494, 1006, 30, 542, 286, 798, 158, 670, 414, 926, 94,
        606, 350, 862, 222, 734, 478, 990, 62, 574, 318, 830, 190, 702, 446, 958, 126, 638, 382,
        894, 254, 766, 510, 1022, 1, 513, 257, 769, 129, 641, 385, 897, 65, 577, 321, 833, 193,
        705, 449, 961, 33, 545, 289, 801, 161, 673, 417, 929, 97, 609, 353, 865, 225, 737, 481,
        993, 17, 529, 273, 785, 145, 657, 401, 913, 81, 593, 337, 849, 209, 721, 465, 977, 49, 561,
        305, 817, 177, 689, 433, 945, 113, 625, 369, 881, 241, 753, 497, 1009, 9, 521, 265, 777,
        137, 649, 393, 905, 73, 585, 329, 841, 201, 713, 457, 969, 41, 553, 297, 809, 169, 681,
        425, 937, 105, 617, 361, 873, 233, 745, 489, 1001, 25, 537, 281, 793, 153, 665, 409, 921,
        89, 601, 345, 857, 217, 729, 473, 985, 57, 569, 313, 825, 185, 697, 441, 953, 121, 633,
        377, 889, 249, 761, 505, 1017, 5, 517, 261, 773, 133, 645, 389, 901, 69, 581, 325, 837,
        197, 709, 453, 965, 37, 549, 293, 805, 165, 677, 421, 933, 101, 613, 357, 869, 229, 741,
        485, 997, 21, 533, 277, 789, 149, 661, 405, 917, 85, 597, 341, 853, 213, 725, 469, 981, 53,
        565, 309, 821, 181, 693, 437, 949, 117, 629, 373, 885, 245, 757, 501, 1013, 13, 525, 269,
        781, 141, 653, 397, 909, 77, 589, 333, 845, 205, 717, 461, 973, 45, 557, 301, 813, 173,
        685, 429, 941, 109, 621, 365, 877, 237, 749, 493, 1005, 29, 541, 285, 797, 157, 669, 413,
        925, 93, 605, 349, 861, 221, 733, 477, 989, 61, 573, 317, 829, 189, 701, 445, 957, 125,
        637, 381, 893, 253, 765, 509, 1021, 3, 515, 259, 771, 131, 643, 387, 899, 67, 579, 323,
        835, 195, 707, 451, 963, 35, 547, 291, 803, 163, 675, 419, 931, 99, 611, 355, 867, 227,
        739, 483, 995, 19, 531, 275, 787, 147, 659, 403, 915, 83, 595, 339, 851, 211, 723, 467,
        979, 51, 563, 307, 819, 179, 691, 435, 947, 115, 627, 371, 883, 243, 755, 499, 1011, 11,
        523, 267, 779, 139, 651, 395, 907, 75, 587, 331, 843, 203, 715, 459, 971, 43, 555, 299,
        811, 171, 683, 427, 939, 107, 619, 363, 875, 235, 747, 491, 1003, 27, 539, 283, 795, 155,
        667, 411, 923, 91, 603, 347, 859, 219, 731, 475, 987, 59, 571, 315, 827, 187, 699, 443,
        955, 123, 635, 379, 891, 251, 763, 507, 1019, 7, 519, 263, 775, 135, 647, 391, 903, 71,
        583, 327, 839, 199, 711, 455, 967, 39, 551, 295, 807, 167, 679, 423, 935, 103, 615, 359,
        871, 231, 743, 487, 999, 23, 535, 279, 791, 151, 663, 407, 919, 87, 599, 343, 855, 215,
        727, 471, 983, 55, 567, 311, 823, 183, 695, 439, 951, 119, 631, 375, 887, 247, 759, 503,
        1015, 15, 527, 271, 783, 143, 655, 399, 911, 79, 591, 335, 847, 207, 719, 463, 975, 47,
        559, 303, 815, 175, 687, 431, 943, 111, 623, 367, 879, 239, 751, 495, 1007, 31, 543, 287,
        799, 159, 671, 415, 927, 95, 607, 351, 863, 223, 735, 479, 991, 63, 575, 319, 831, 191,
        703, 447, 959, 127, 639, 383, 895, 255, 767, 511, 1023,
    ], // width 10
];

fn flip_horizontal(board: u64, width: usize, height: usize) -> u64 {
    let mut result = board;
    for i in 0..height {
        for j in 0..(width / 2) {
            let pos1 = i * width + j;
            let pos2 = i * width + (width - 1 - j);

            let bit1 = (result >> pos1) & 1;
            let bit2 = (result >> pos2) & 1;

            result &= !(1u64 << pos1);
            result &= !(1u64 << pos2);

            result |= bit2 << pos1;
            result |= bit1 << pos2;
        }
    }
    result
}

fn flip_vertical(board: u64, width: usize, height: usize) -> u64 {
    let mut result = board;
    for i in 0..(height / 2) {
        let row1_start = i * width;
        let row2_start = (height - 1 - i) * width;
        let row_mask = (1u64 << width) - 1;

        let row1 = (result >> row1_start) & row_mask;
        let row2 = (result >> row2_start) & row_mask;

        result &= !(row_mask << row1_start);
        result &= !(row_mask << row2_start);

        result |= row2 << row1_start;
        result |= row1 << row2_start;
    }
    result
}

fn rotate_180(board: u64, width: usize, height: usize) -> u64 {
    let mut result = 0u64;
    for row in 0..height {
        let row_start = row * width;
        let row_mask = (1u64 << width) - 1;
        let row_val = ((board >> row_start) & row_mask) as usize;
        let flipped_row = FLIPLR[width][row_val];
        let new_row = height - 1 - row;
        result |= flipped_row << (new_row * width);
    }
    result
}

fn rotate_90(board: u64, width: usize, height: usize) -> u64 {
    let mut result = 0u64;
    for i in 0..height {
        for j in 0..width {
            let old_pos = i * width + j;
            let new_pos = j * height + (height - 1 - i);
            if board & (1u64 << old_pos) != 0 {
                result |= 1u64 << new_pos;
            }
        }
    }
    result
}

fn rotate_270(board: u64, width: usize, height: usize) -> u64 {
    let mut result = 0u64;
    for i in 0..height {
        for j in 0..width {
            let old_pos = i * width + j;
            let new_pos = (width - 1 - j) * height + i;
            if board & (1u64 << old_pos) != 0 {
                result |= 1u64 << new_pos;
            }
        }
    }
    result
}

fn transpose(board: u64, width: usize, height: usize) -> u64 {
    let mut result = 0u64;
    for i in 0..height {
        for j in 0..width {
            let old_pos = i * width + j;
            let new_pos = j * height + i;
            if board & (1u64 << old_pos) != 0 {
                result |= 1u64 << new_pos;
            }
        }
    }
    result
}

struct SymmetryEngine {
    width: usize,
    height: usize,
}

impl SymmetryEngine {
    fn new(width: usize, height: usize, _use_lookup: bool) -> Self {
        Self { width, height }
    }

    fn canonical_form(&self, board: u64, use_symmetries: bool) -> u64 {
        if !use_symmetries {
            return board;
        }

        let mut variants = vec![
            board,
            flip_horizontal(board, self.width, self.height),
            flip_vertical(board, self.width, self.height),
            rotate_180(board, self.width, self.height),
        ];

        // Add square-only symmetries for square boards
        if self.width == self.height {
            variants.push(rotate_90(board, self.width, self.height));
            variants.push(rotate_270(board, self.width, self.height));
            variants.push(transpose(board, self.width, self.height));
            // Anti-transpose: transpose + 180 rotation
            let transposed = transpose(board, self.width, self.height);
            variants.push(rotate_180(transposed, self.width, self.height));
        }

        *variants.iter().min().unwrap()
    }
}

fn grundy(
    board: u64,
    width: usize,
    height: usize,
    v_domino: u64,
    use_symmetries: bool,
    is_misere: bool,
    symmetry_engine: &SymmetryEngine,
    cache: &mut FxHashMap<u64, u8>,
) -> u8 {
    let key = symmetry_engine.canonical_form(board, use_symmetries);

    if let Some(&value) = cache.get(&key) {
        return value;
    }

    let mut mex_set = vec![false; 64];
    let mut max_mex = 0;
    let mut has_moves = false;

    // Try horizontal dominoes
    for pos in 0..(width * height) {
        let col = pos % width;
        if col < width - 1 && (board & (H_DOMINO << pos)) == 0 {
            has_moves = true;
            let new_board = board | (H_DOMINO << pos);
            let child_grundy = grundy(
                new_board,
                width,
                height,
                v_domino,
                use_symmetries,
                is_misere,
                symmetry_engine,
                cache,
            );
            if (child_grundy as usize) < mex_set.len() {
                mex_set[child_grundy as usize] = true;
                if child_grundy > max_mex {
                    max_mex = child_grundy;
                }
            }
        }
    }

    // Try vertical dominoes
    for pos in 0..(width * (height - 1)) {
        if (board & (v_domino << pos)) == 0 {
            has_moves = true;
            let new_board = board | (v_domino << pos);
            let child_grundy = grundy(
                new_board,
                width,
                height,
                v_domino,
                use_symmetries,
                is_misere,
                symmetry_engine,
                cache,
            );
            if (child_grundy as usize) < mex_set.len() {
                mex_set[child_grundy as usize] = true;
                if child_grundy > max_mex {
                    max_mex = child_grundy;
                }
            }
        }
    }

    // Handle terminal positions (no moves available)
    let final_value = if !has_moves {
        // Terminal position:
        // Normal play: losing position (0)
        // Misere play: winning position (1) - you want to force opponent to make last move
        if is_misere {
            1
        } else {
            0
        }
    } else {
        // Find mex (minimum excludant) for non-terminal positions
        let mut mex_value = 0;
        while mex_value <= max_mex && mex_set[mex_value as usize] {
            mex_value += 1;
        }
        mex_value
    };

    cache.insert(key, final_value);
    final_value
}

fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() < 3 || args.len() > 6 {
        println!(
            "Usage: {} height width [--symmetries] [--lookup] [--misere]",
            args[0]
        );
        return;
    }

    let height: usize = args[1].parse().expect("Invalid height");
    let width: usize = args[2].parse().expect("Invalid width");
    let use_symmetries = args.contains(&"--symmetries".to_string());
    let use_lookup = args.contains(&"--lookup".to_string());
    let is_misere = args.contains(&"--misere".to_string());

    if height * width >= 64 {
        println!("Board too large (max 63 cells)");
        return;
    }

    let v_domino = 1u64 | (1u64 << width);

    println!(
        "CRAM[{}x{}]{}{}{}",
        height,
        width,
        if use_symmetries {
            " with symmetries"
        } else {
            ""
        },
        if use_lookup {
            " with lookup tables"
        } else {
            ""
        },
        if is_misere { " (Misere)" } else { "" }
    );

    let symmetry_engine = SymmetryEngine::new(width, height, use_lookup);
    let mut cache = FxHashMap::default();
    let start = Instant::now();

    let g_value = grundy(
        0,
        width,
        height,
        v_domino,
        use_symmetries,
        is_misere,
        &symmetry_engine,
        &mut cache,
    );

    let duration = start.elapsed();

    let winner_msg = if is_misere {
        if g_value == 0 {
            "The second player can always win."
        } else {
            "The first player can always win."
        }
    } else {
        if g_value == 0 {
            "The second player can always win."
        } else {
            "The first player can always win."
        }
    };

    println!(
        "The g-value of the starting position is {}. {}",
        g_value, winner_msg
    );

    println!("Number of positions: {}", cache.len());
    println!("Duration: {} ms", duration.as_millis());
}
