-module(cram).

-export([g/2, print/2]).

% linear cram recursion
g(1,W) when W < 2 -> 0;
g(1,W) -> mex(lists:map(fun(I) -> g(1,I) bxor g(1,W-I-2) end, lists:seq(0, trunc(W/2))));

% CRAM[2,N] has g-values 0 for N even and 1 for N odd
g(2,W) -> W rem 2;

% all even by even boards have a g-value 0 due to a symmetry argument
g(X,Y) when X rem 2 =:= 0, Y rem 2 =:= 0 -> 0;

% rotating the board by 90 degrees doesn't change its g-values
g(X,Y) -> g(Y,X).

% mex(N) = smallest positive number (including 0) not in N
head([X|_]) -> X.
mex(N) -> head(lists:seq(0, length(N)) -- N).

% for string formatting
winner(X) -> 
	if
        X =:= 0 -> "second";
        true -> "first"
    end.

% output
print(H,W) when W > 1, H > 2, H rem 2 > 0 -> io:format("~nCRAM[~Bx~B]~nBoard size ~B x ~B is not supported yet.~n", [H, W, H, W]);
print(H,W) when H > 1, W > 2, W rem 2 > 0 -> io:format("~nCRAM[~Bx~B]~nBoard size ~B x ~B is not supported yet.~n", [H, W, H, W]);
print(H,W) -> io:format("~nCRAM[~Bx~B]~nThe g-value of the starting position is ~B. The ~s player can always win.~n", [H, W, g(H,W), winner(g(H,W))]).