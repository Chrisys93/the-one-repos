clear
R = dlmread('default_scenario_RepoMessageLocationReport.txt', ' ', 0, 1);
M = dlmread('default_scenario_MobileMessageLocationReport.txt', ' ', 0, 1);
[r1, c1] = size(R);
[r2, c2] = size(M);
maxstor1 = zeros(c1, 0);
maxstor2 = zeros(c2, 0);

for i = 1:r1
    maxstor1(i) = max(R(i, :));
end
maxval1 = max(maxstor1);

for i = 1:r1
    maxstor2(i) = max(M(i, :));
end
maxval2 = max(maxstor2);

figure
bar(maxstor1);
title('Bar plot of Repos Buffer(!) Usage')
xlabel('Time(s)') 
ylabel('Space used(B)')

figure
bar(maxstor2);
title('Bar plot of Mobile Nodes Buffer(!) Usage')
xlabel('Time(s)') 
ylabel('Space used(B)')

%figure
%bar3(M);

%figure
%bar3(R);