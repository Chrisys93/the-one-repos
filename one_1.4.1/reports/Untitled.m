clear
M = dlmread('default_scenario_MessageLocationReport.txt', ' ', 0, 1);
[r, c] = size(M);
maxstor = zeros(c, 0);

for i = 1:r
    maxstor(i) = max(M(i, :));
end
maxval = max(maxstor);

figure
bar(maxstor);

%figure
%bar3(M);