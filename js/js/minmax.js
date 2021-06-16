// for (non-misere) domineering only

function fitness(b)
{
	return succ(b, 2, false, DOM).size - succ(b, 1, false, DOM).size;
}

function max(depth, b) {
	if (depth == 0)
	{
		return fitness(b);
	}
	var value = -100;
	var successors = succ(b, 2, false, DOM);
	if (successors.length == 0)
	{
		return value + 1;
	}
	for (var b1 of successors) {
		var newValue = min(depth - 1, b1);
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

function min(depth, b) {
	if (depth == 0)
	{
		return fitness(b);
	}
	var value = 100;
	var successors = succ(b, 1, false, DOM);
	if (successors.length == 0)
	{
		return eval - 1;
	}
	for (var b1 of successors) {
		var newValue = max(depth - 1, b1);
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