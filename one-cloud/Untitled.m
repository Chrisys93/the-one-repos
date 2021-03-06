clear
R1 = dlmread('RDMR1', ' ', 0, 1);
M1 = dlmread('MDMR1', ' ', 0, 1);
S1 = dlmread('RSMLR1', ' ', 0, 2);
R2 = dlmread('RDMR2', ' ', 0, 1);
M2 = dlmread('MDMR2', ' ', 0, 1);
S2 = dlmread('RSMLR2', ' ', 0, 2);
R3 = dlmread('RDMR3', ' ', 0, 1);
M3 = dlmread('MDMR3', ' ', 0, 1);
S3 = dlmread('RSMLR3', ' ', 0, 2);
R4 = dlmread('RDMR4', ' ', 0, 1);
M4 = dlmread('MDMR4', ' ', 0, 1);
S4 = dlmread('RSMLR4', ' ', 0, 2);
R5 = dlmread('RDMR5', ' ', 0, 1);
M5 = dlmread('MDMR5', ' ', 0, 1);
S5 = dlmread('RSMLR5', ' ', 0, 2);
[r1, c1] = size(R1);
[r2, c2] = size(M1);
[r3, c3] = size(S1);
maxstorR1 = zeros(c1, 0);
maxstorM1 = zeros(c2, 0);
maxstor = zeros(c3, 0);

for i = 1:r1
    maxstorR1(i) = max(R1(i, :));
end
maxval1 = max(maxstorR1);

for i = 1:r2
    maxstorM1(i) = max(M1(i, :));
end
maxval2 = max(maxstorM1);

for inc = 1:5
    if inc == 1
        S = S1;
    end
    
    if inc == 2
        S = S2;
    end
    
    if inc == 3
        S = S3;
    end
    if inc == 4
        S = S4;
    end
    
    if inc == 5
        S = S5;
    end
    
    s = 10000;
    col = 1;
    for i = 1:r3
        maxstor(i) = max(S(i, :));
        if maxstor(i) >= 10000 && i < s
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

    maxval3 = max(maxstor);
    
    if inc == 1
        maxstorS1 = maxstor;
        fillpercS1 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        fillpercerrbarS1 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    if inc == 2
        maxstorS2 = maxstor;
        fillpercS2 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        fillpercerrbarS2 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    if inc == 3
        maxstorS3 = maxstor;
        fillpercS3 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        fillpercerrbarS3 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    if inc == 4
        maxstorS1 = maxstor;
        fillpercS4 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        fillpercerrbarS4 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    if inc == 5
        maxstorS5 = maxstor;
        fillpercS5 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        fillpercerrbarS5 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    
                
end

figure
hold on
errorbar([25,50,100], fillpercerrbarS1(:,2), fillpercerrbarS1(:,2)-fillpercerrbarS1(:,1), fillpercerrbarS1(:,3)-fillpercerrbarS1(:,2), '--k', 'LineWidth', 2);
errorbar([25,50,100], fillpercerrbarS2(:,2), fillpercerrbarS2(:,2)-fillpercerrbarS2(:,1), fillpercerrbarS2(:,3)-fillpercerrbarS2(:,2), '--b', 'LineWidth', 2);
errorbar([25,50,100], fillpercerrbarS3(:,2), fillpercerrbarS3(:,2)-fillpercerrbarS3(:,1), fillpercerrbarS3(:,3)-fillpercerrbarS3(:,2), '--y', 'LineWidth', 2);
errorbar([25,50,100], fillpercerrbarS4(:,2), fillpercerrbarS4(:,2)-fillpercerrbarS4(:,1), fillpercerrbarS4(:,3)-fillpercerrbarS4(:,2), '--m', 'LineWidth', 2);
errorbar([25,50,100], fillpercerrbarS5(:,2), fillpercerrbarS5(:,2)-fillpercerrbarS5(:,1), fillpercerrbarS5(:,3)-fillpercerrbarS5(:,2), '--r', 'LineWidth', 2);
title('Percentage of repositories filled up to indicated values (with mode and max no. of filled repos)');
xlabel('Repo storage filled (%)');
ylabel('Quantity of repos filled (%)');
axis([0 125 0 100])
hold off


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
bar(maxstorR1);
title('Bar plot of Repos Deleted Messages')
xlabel('Time(s)') 
ylabel('Space used(MB)')

figure
bar(maxstorM1);
title('Bar plot of Mobile Nodes Deleted Messages')
xlabel('Time(s)') 
ylabel('Space used(MB)')

figure
bar(maxstor, 0.5);
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
stem3(S1, ':.');
title('3D Stem plot of Repos Storage Usage')
xlabel('Repo Number') 
ylabel('Time(s)')
zlabel('Space used(MB)')

figure
stem3(M1, ':.');
title('3D Stem plot of Mobile Deleted Messages')
xlabel('Repo Number') 
ylabel('Time(s)')
zlabel('Messages')

figure
stem3(R1, ':.');
title('3D Stem plot of Repos Dleted Messages')
xlabel('Repo Number') 
ylabel('Time(s)')
zlabel('Messages')
