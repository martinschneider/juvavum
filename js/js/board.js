function key(b) {
  var key = 0;
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < w * h; i++) {
    if (b[i % w][Math.floor(i / w)]) {
      key += (1 << i);
    }
  }
  return key;
}

function countEmptyFields(b) {
  var count = 0;
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < w * h; i++) {
    if (!b[i % w][Math.floor(i / w)]) {
      count++;
    }
  }
  return count;
 }
