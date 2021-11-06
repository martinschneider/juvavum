use std::env;
use std::collections::HashSet;

const H_D: i128 = 3;

fn main() {
    let args: Vec<String> = env::args().collect();
    let h: i8;
    let w: i8;
    match args.len() {
        3 => {
            let height = &args[1];
            let width = &args[2];
            h = match height.parse() {
                Ok(n) => {
                    n
                },
                Err(_) => {
                    eprintln!("error: height is not an integer");
                    return;
                },
            };
            w = match width.parse() {
                Ok(n) => {
                    n
                },
                Err(_) => {
                    eprintln!("error: width is not an integer");
                    return;
                },
            };
        }
        _ => {
            println!("Usage: {} height width", args[0]);
            return;
        }
    }
    println!("{:?}",pos(h,w,0,1 | 1 << w, HashSet::new()).len());
}

fn pos(h: i8, w: i8, b: i128, v_d: i128, mut set: HashSet<i128>) -> HashSet<i128>{
    if !set.contains(&b) {
        set.insert(b);
        for i in 0..h*w {
          if i%w!=w-1 && (b & H_D << i == 0) {
            set = pos(h, w, b | H_D << i, v_d, set);
          }
          if i<w*(h-1) && (b & (v_d << i) == 0) {
            set = pos(h, w, b | v_d << i, v_d, set);
          }
        }
      }
    return set;
}