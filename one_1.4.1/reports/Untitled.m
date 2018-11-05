clear
R = dlmread('default_scenario_RepoMessageLocationReport.txt', ' ', 0, 1);
M = dlmread('default_scenario_MobileMessageLocationReport.txt', ' ', 0, 1);
S = dlmread('default_scenario_RepoStoredMessageLocationReport.txt', ' ', 0, 2);
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
col = 1;
for i = 1:r3
    maxstor3(i) = max(S(i, :));
    if maxstor3(i) >= 10000 && i < s
        c0 = 1;
        c50 = 1;
        for c = 1:c3
            if S(i, c) >= 5000
                fillperc50_100(c50) = S(i, c)/10000*100;
                c50=c50+1;
            else
                fillperc0_50(c0) = S(i, c)/10000*100;
                c0=c0+1;
            end
        end
        figure
        bar(S(i, :));
        s = i; 
        %s = 1434 for 1000 cars
        %s = 1266 for 40 cars
    end
    
    c100 = 1;
    for c = 1:c3
        if S(i, c) >= 9900
            fill100(c100) = 1;
        else
            fill100(c100) = 0;
        end
        c100=c100+1;
    end
    fillperc100(col) = sum(fill100)/40*100;
    
    c50 = 1;
    for c = 1:c3
        if S(i, c) >= 5000
            fill50(c50) = 1;
        else
            fill50(c50) = 0;
        end
        c50=c50+1;
    end
    fillperc50(col) = sum(fill50)/40*100;
    
    c25 = 1;
    for c = 1:c3
        if S(i, c) >= 2500
            fill25(c25) = 1;
        else
            fill25(c25) = 0;
        end
        c25=c25+1;
    end
    fillperc25(col) = (sum(fill25))/40*100;
    col = col + 1;
end
maxval3 = max(maxstor3);

figure
bar(fillperc100);
title('Percentage of repositories filled up to 100%');
xlabel('Time (s)');
ylabel('Repositories which used about 100% storage (%)');

figure
bar(fillperc50);
title('Percentage of repositories filled up to 50%');
xlabel('Time (s)');
ylabel('Repositories which used more than 50% storage (%)');

figure
bar(fillperc25);
title('Percentage of repositories filled up to 25%');
xlabel('Time (s)');
ylabel('Repositories which used more than 25% storage (%)');

figure
bar(maxstor1);
title('Bar plot of Repos Buffer(!) Usage')
xlabel('Time(s)') 
ylabel('Space used(MB)')

figure
bar(maxstor2);
title('Bar plot of Mobile Nodes Buffer(!) Usage')
xlabel('Time(s)') 
ylabel('Space used(MB)')

figure
bar(maxstor3, 0.5);
title('Bar plot of Repos Storage Usage')
xlabel('Time(s)') 
ylabel('Space used(MB)')

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
title('3D Stem plot of Repos Storage Usage')
xlabel('Repo Number') 
ylabel('Time(s)')
zlabel('Space used(MB)')

