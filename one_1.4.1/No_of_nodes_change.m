% Make a function out of this, that takes the reports folder address and
% the number of runs as variables;

clear
M1 = dlmread('reports2/MDMR1', ' ', 0, 2);
S1 = dlmread('reports2/RAMR1', ' ', 0, 2);
PB1 = dlmread('reports2/RPBWR1', ' ', 0, 2);
UB1 = dlmread('reports2/RUPBWR1', ' ', 0, 2);
SB1 = dlmread('reports2/RSBWR1', ' ', 0, 2);
RI1 = dlmread('reports2/RISR1', ' ', 0, 2);
RP1 = dlmread('reports2/RPrMR1', ' ', 0, 2);
RS1 = dlmread('reports2/RSMR1', ' ', 0, 2);

M2 = dlmread('reports2/MDMR2', ' ', 0, 2);
S2 = dlmread('reports2/RAMR2', ' ', 0, 2);
UB2 = dlmread('reports2/RUPBWR2', ' ', 0, 2);
PB2 = dlmread('reports2/RPBWR2', ' ', 0, 2);
SB2 = dlmread('reports2/RSBWR2', ' ', 0, 2);
RI2 = dlmread('reports2/RISR2', ' ', 0, 2);
RP2 = dlmread('reports2/RPrMR2', ' ', 0, 2);
RS2 = dlmread('reports2/RSMR2', ' ', 0, 2);

M3 = dlmread('reports2/MDMR3', ' ', 0, 2);
S3 = dlmread('reports2/RAMR3', ' ', 0, 2);
PB3 = dlmread('reports2/RPBWR3', ' ', 0, 2);
UB3 = dlmread('reports2/RUPBWR3', ' ', 0, 2);
SB3 = dlmread('reports2/RSBWR3', ' ', 0, 2);
RI3 = dlmread('reports2/RISR3', ' ', 0, 2);
RP3 = dlmread('reports2/RPrMR3', ' ', 0, 2);
RS3 = dlmread('reports2/RSMR3', ' ', 0, 2);
% R6 = dlmread('reports1/RDMR6', ' ', 0, 1);
% M6 = dlmread('reports1/MDMR6', ' ', 0, 1);
% S6 = dlmread('reports1/RSMLR6', ' ', 0, 2);
[r11, c11] = size(R1);
[r12, c12] = size(R2);
[r21, c21] = size(M1);
[r22, c22] = size(M2);
[r31, c31] = size(S1);
[r32, c32] = size(S2);
maxstorR1 = zeros(c11, 0);
maxstorM1 = zeros(c21, 0);
maxstor = zeros(c31, 0);

for i = 1:r11
    maxstorR1(i) = max(R1(i, :));
end
maxval1 = max(maxstorR1);

for i = 1:r21
    maxstorM1(i) = max(M1(i, :));
end
maxval2 = max(maxstorM1);

for inc = 2
    if inc == 1
        S = S1;
    end
    
    if inc == 2
        S = S2;
    end
    
    if inc == 3
        S = S3;
    end
%     
%     if inc == 4
%         S = S4;
%     end
%     
%     if inc == 5
%         S = S5;
%     end
%     
%     if inc == 6
%         S = S6;
%     end
%     
%     if inc == 7
%         S = S7;
%     end
%     
%     if inc == 8
%         S = S8;
%     end
%     
%     if inc == 9
%         S = S9;
%     end
%     
%     if inc == 10
%         S = S10;
%     end
%     
%     if inc == 11
%         S = S11;
%     end
%     
%     if inc == 12
%         S = S12;
%     end
%     
%     if inc == 13
%         S = S13;
%     end
%     
%     if inc == 14
%         S = S14;
%     end
%     
%     if inc == 15
%         S = S15;
%     end
    
    s = 10000;
    col = 1;
    for i = 1:r31
        maxstor(i) = max(S(i, :));
        if maxstor(i) >= 15000 && i < s
            c0 = 1;
            c50 = 1;
            for c = 1:c31
                if S(i, c) >= 7500
                    fillperc50_100(c50) = S(i, c)/15000*100;
                    c50=c50+1;
                else
                    fillperc0_50(c0) = S(i, c)/15000*100;
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
        for c = 1:c31
            if S(i, c) >= 14000
                fill100(c100) = 1;
            else
                fill100(c100) = 0;
            end
            c100=c100+1;
        end
        filled100(i, :) = fill100;        
        fillperc100(col) = sum(fill100)/40*100;

        c50 = 1;
        for c = 1:c31
            if S(i, c) >= 7500
                fill50(c50) = 1;
            else
                fill50(c50) = 0;
            end
            c50=c50+1;
        end
        filled50(i, :) = fill50;
        fillperc50(col) = sum(fill50)/40*100;

        c25 = 1;
        for c = 1:c31
            if S(i, c) >= 4507
                fill25(c25) = 1;
            else
                fill25(c25) = 0;
            end
            c25=c25+1;
        end
        filled25(i, :) = fill25;
        fillperc25(col) = (sum(fill25))/40*100;
        col = col + 1;
    end

    maxval3 = max(maxstor);
    
    if inc == 1
        c3 = c31;
        r3 = r31;
        maxstorS1 = maxstor;
        fillpercS1 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        filled100S1 = filled100;
        filled50S1 = filled50;
        filled25S1 = filled25;
        for repo = 1:c3
            reposfillS1(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS1(repo);           
        end
        fillpercerrbarS1 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    if inc == 2
        c3 = c32;
        r3 = r32;
        maxstorS2 = maxstor;
        fillpercS2 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS2(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS2(repo);           
        end
        fillpercerrbarS2 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
    
    if inc == 3
        maxstorS3 = maxstor;
        fillpercS3 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS3(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS3(repo);           
        end
        fillpercerrbarS3 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
                            mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
                            mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
    end
%     
%     if inc == 4
%         maxstorS1 = maxstor;
%         fillpercS4 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS4(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS4(repo);           
%         end
%         fillpercerrbarS4 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 5
%         maxstorS5 = maxstor;
%         fillpercS5 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS5(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS5(repo);
%         end
%         fillpercerrbarS5 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 6
%         maxstorS6 = maxstor;
%         fillpercS6 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS6(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS6(repo);
%         end
%         fillpercerrbarS6 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     for repo = 1:c3
%         
%     end
                
end

% figure
% hold on
% errorbar([25,50,100], fillpercerrbarS1(:,2), fillpercerrbarS1(:,2)-fillpercerrbarS1(:,1), fillpercerrbarS1(:,3)-fillpercerrbarS1(:,2), '--k', 'LineWidth', 2);
% errorbar([25,50,100], fillpercerrbarS2(:,2), fillpercerrbarS2(:,2)-fillpercerrbarS2(:,1), fillpercerrbarS2(:,3)-fillpercerrbarS2(:,2), '--b', 'LineWidth', 2);
% errorbar([25,50,100], fillpercerrbarS3(:,2), fillpercerrbarS3(:,2)-fillpercerrbarS3(:,1), fillpercerrbarS3(:,3)-fillpercerrbarS3(:,2), '--y', 'LineWidth', 2);
% errorbar([25,50,100], fillpercerrbarS4(:,2), fillpercerrbarS4(:,2)-fillpercerrbarS4(:,1), fillpercerrbarS4(:,3)-fillpercerrbarS4(:,2), '--m', 'LineWidth', 2);
% errorbar([25,50,100], fillpercerrbarS5(:,2), fillpercerrbarS5(:,2)-fillpercerrbarS5(:,1), fillpercerrbarS5(:,3)-fillpercerrbarS5(:,2), '--r', 'LineWidth', 2);
% errorbar([25,50,100], fillpercerrbarS6(:,2), fillpercerrbarS6(:,2)-fillpercerrbarS6(:,1), fillpercerrbarS6(:,3)-fillpercerrbarS6(:,2), '--g', 'LineWidth', 2);
% title('Percentage of repositories filled up according to storage depletion ratio');
% xlabel('Repo storage filled (%)');
% ylabel('Quantity of repos filled (%)');
% axis([0 125 0 100])
% legend('2 msg/ 3 s', '2 msg/2 s', '2 msg/s', '1 msg/3 s', '1 msg/2 s', '1 msg/s');
% hold off

% figure
% hold on
% bar(reposfill);
% title('Repositories storage used according to storage depletion ratio');
% xlabel('Repo number');
% ylabel('Repo storage used (MB)');
% legend('2 msg/ 3 s', '2 msg/2 s', '2 msg/s', '1 msg/3 s', '1 msg/2 s', '1 msg/s');
% hold off


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

storage_29 = [S1(:, 29), S2(:, 29)];

figure
plot(storage_29);
legend('200 cars', '500 cars');

deleted_29 = [max(R1(:, 29)), max(R2(:, 29)), max(R3(:, 29)), max(R4(:, 29))];

figure
plot([1 2 3 4], deleted_29);

mdeleted1 = M1(10000, :);
mdeleted2 = M2(10000, :);
addr1 = 80:334;
addr2 = 80:634;

figure
subplot(1,2,1);
bar(addr1, mdeleted1);
title('200 cars in environment');
ylabel('Messages deleted');
xlabel('Mobile node address');
subplot(1,2,2);
bar(addr2, mdeleted2);
title('500 cars in environment');
xlabel('Mobile node address');
