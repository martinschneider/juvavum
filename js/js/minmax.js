// simple min-max algorithm
// for (non-misere) domineering only

// value of a position = number of a player's possible moves minus the number of the opponent's possible moves
function fitness(b, currMov)
{
	return succ(b, 2, false, DOM).size - succ(b, 1, false, DOM).size;
}

function max(depth, b, currMov) {
	if (depth == 0)
	{
		return fitness(b, currMov);
	}
	var value = -100;
	var successors = succ(b, currMov, false, DOM);
	if (successors.size == 0)
	{
		return value + 1;
	}
	for (var b1 of successors) {
		var newValue = min(depth - 1, b1, currMov + 1);
		if (newValue > value) {
			value = newValue;
			if (depth == MIN_MAX_DEPTH)
			{
				best = b1;
			}
		}
	}
	return value;
};

function min(depth, b, currMov) {
	if (depth == 0)
	{
		return fitness(b, currMov);
	}
	var value = 100;
	var successors = succ(b, currMov, false, DOM);
	if (successors.size == 0)
	{
		return value - 1;
	}
	for (var b1 of successors) {
		var newValue = max(depth - 1, b1, currMov + 1);
		if (newValue < value) {
			value = newValue;
			if (depth == MIN_MAX_DEPTH)
			{
				best = b1;
			}
		}
	}
	return value;
};