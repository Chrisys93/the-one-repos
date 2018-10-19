clear
R = dlmread('default_scenario_RepoMessageLocationReport.txt', ' ', 0, 1);
M = dlmread('default_scenario_MobileMessageLocationReport.txt', ' ', 0, 1);
S = dlmread('default_scenario_RepoStoredMessageLocationReport.txt', ' ', 1, 1);
[r1, c1] = size(R);
[r2, c2] = size(M);
[r3, c3] = size(S);
maxstor1 = zeros(c1, 0);
maxstor2 = zeros(c2, 0);
maxstor3 = zeros(c3, 0);

for i = 1:r1
    maxstor1(i) = max(R(i, :));
end
maxval1 = max(maxstor1);

for i = 1:r2
    maxstor2(i) = max(M(i, :));
end
maxval2 = max(maxstor2);

s = 10000;
for i = 1:r3
    maxstor3(i) = max(S(i, :));
    if maxstor3(i) >= 10000 && i < s
        figure
        bar(S(i, :));
        s = i;
    end
end
maxval3 = max(maxstor3);

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

figure
bar(maxstor3, 0.5);
title('Bar plot of Repos Stored Nodes Storage Usage')
xlabel('Time(s)') 
ylabel('Space used(B)')

%figure
%bar3(M);

%figure
%bar3(R);

%figure
%bar3(S, 0.5);

%figure
%bmesh(S);

figure
stem3(S, ':.');

