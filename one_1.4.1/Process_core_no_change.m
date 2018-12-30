% Make a function out of this, that takes the reports folder address and
% the number of runs as variables;

clear
M1 = dlmread('reports6/MDMR1', ' ', 0, 2);
S1 = dlmread('reports6/RAMR1', ' ', 0, 2);
PB1 = dlmread('reports6/RPBWR1', ' ', 0, 2);
UB1 = dlmread('reports6/RUPBWR1', ' ', 0, 2);
SB1 = dlmread('reports6/RSBWR1', ' ', 0, 2);
RI1 = dlmread('reports6/RISR1', ' ', 0, 2);
RP1 = dlmread('reports6/RPrMR1', ' ', 0, 2);
RS1 = dlmread('reports6/RSMR1', ' ', 0, 2);

M2 = dlmread('reports6/MDMR2', ' ', 0, 2);
S2 = dlmread('reports6/RAMR2', ' ', 0, 2);
UB2 = dlmread('reports6/RUPBWR2', ' ', 0, 2);
PB2 = dlmread('reports6/RPBWR2', ' ', 0, 2);
SB2 = dlmread('reports6/RSBWR2', ' ', 0, 2);
RI2 = dlmread('reports6/RISR2', ' ', 0, 2);
RP2 = dlmread('reports6/RPrMR2', ' ', 0, 2);
RS2 = dlmread('reports6/RSMR2', ' ', 0, 2);

M3 = dlmread('reports6/MDMR3', ' ', 0, 2);
S3 = dlmread('reports6/RAMR3', ' ', 0, 2);
PB3 = dlmread('reports6/RPBWR3', ' ', 0, 2);
UB3 = dlmread('reports6/RUPBWR3', ' ', 0, 2);
SB3 = dlmread('reports6/RSBWR3', ' ', 0, 2);
RI3 = dlmread('reports6/RISR3', ' ', 0, 2);
RP3 = dlmread('reports6/RPrMR3', ' ', 0, 2);
RS3 = dlmread('reports6/RSMR3', ' ', 0, 2);

M4 = dlmread('reports6/MDMR4', ' ', 0, 2);
S4 = dlmread('reports6/RAMR4', ' ', 0, 2);
PB4 = dlmread('reports6/RPBWR4', ' ', 0, 2);
UB4 = dlmread('reports6/RUPBWR4', ' ', 0, 2);
SB4 = dlmread('reports6/RSBWR4', ' ', 0, 2);
RI4 = dlmread('reports6/RISR4', ' ', 0, 2);
RP4 = dlmread('reports6/RPrMR4', ' ', 0, 2);
RS4 = dlmread('reports6/RSMR4', ' ', 0, 2);

% M5 = dlmread('reports6/MDMR5', ' ', 0, 2);
% S5 = dlmread('reports6/RAMR5', ' ', 0, 2);
% PB5 = dlmread('reports6/RPBWR5', ' ', 0, 2);
% UB5 = dlmread('reports6/RUPBWR5', ' ', 0, 2);
% SB5 = dlmread('reports6/RSBWR5', ' ', 0, 2);
% RI5 = dlmread('reports6/RISR5', ' ', 0, 2);
% RP5 = dlmread('reports6/RPrMR5', ' ', 0, 2);
% RS5 = dlmread('reports6/RSMR5', ' ', 0, 2);

% M6 = dlmread('reports1/MDMR6', ' ', 0, 1);
% S6 = dlmread('reports1/RSMLR6', ' ', 0, 2);
[r2, c2] = size(M1);
[r3, c3] = size(S1);
maxstorM1 = zeros(c2, 0);
maxstor = zeros(c3, 0);

for i = 1:r2
    maxstorM1(i) = max(M1(i, :));
end
maxval2 = max(maxstorM1);

for inc = 4
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
    
    s = 10000;
    col = 1;
    for i = 1:r3
        maxstor(i) = max(S(i, :));
        if maxstor(i) >= 15000 && i < s
            c0 = 1;
            c50 = 1;
            for c = 1:c3
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
        for c = 1:c3
            if S(i, c) >= 14000
                fill100(c100) = 1;
            else
                fill100(c100) = 0;
            end
            c100=c100+1;
        end
        filled100(i, :) = fill100;        
        fillperc100(col) = sum(fill100)/80*100;

        c50 = 1;
        for c = 1:c3
            if S(i, c) >= 7500
                fill50(c50) = 1;
            else
                fill50(c50) = 0;
            end
            c50=c50+1;
        end
        filled50(i, :) = fill50;
        fillperc50(col) = sum(fill50)/80*100;

        c25 = 1;
        for c = 1:c3
            if S(i, c) >= 4507
                fill25(c25) = 1;
            else
                fill25(c25) = 0;
            end
            c25=c25+1;
        end
        filled25(i, :) = fill25;
        fillperc25(col) = (sum(fill25))/80*100;
        col = col + 1;
    end

    maxval3 = max(maxstor);
    %TODO:
    % Make functions for these next time!
    if inc == 1
        maxstorS1 = maxstor;
        fillpercS1 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        filled100S1 = filled100;
        filled50S1 = filled50;
        filled25S1 = filled25;
        for repo = 1:c3
            reposfillS1(repo, :) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS1(repo);           
        end
%         fillerrbarS1 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 2
        maxstorS2 = maxstor;
        fillpercS2 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS2(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS2(repo);           
        end
%        fillerrbarS3 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 3
        maxstorS3 = maxstor;
        fillpercS3 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS3(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS3(repo);           
        end
%         fillerrbarS3 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 4
        maxstorS4 = maxstor;
        fillpercS4 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            upspeeds4(repo, :) = [mean(SB4(:, repo)), mean(UB4(:, repo)), mean(PB4(:, repo))];
            reposfillS4(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS4(repo);           
        end
%         fillerrbarS4 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    fillerrbar(:, inc) = [sum(filled100(r3, :)), sum(filled50(r3, :)), sum(filled25(r3, :))];
%     
%     if inc == 6
%         maxstorS6 = maxstor;
%         fillpercS6 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS6(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS6(repo);
%         end
%        fillerrbarS1 = [sum(fill100(:)), sum(fill50(:)), sum(fill25(:))];
%     end
%     
%     for repo = 1:c3
%         
%     end

%fillerrorbar = [fillerrbarS1(:); fillerrbarS2(:); fillerrbarS3(:); fillerrbarS4(:); fillerrbarS5(:)];
                %fillerrbarS6];
end



% figure
% hold on
% errorbar([25,50,100], fillerrbar(:,2), fillerrbar(:,2)-fillerrbar(:,1), fillerrbar(:,3)-fillpercerrbar(:,2), '--k', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS2(:,2), fillerrbarS2(:,2)-fillerrbarS2(:,1), fillerrbarS2(:,3)-fillpercerrbarS2(:,2), '--b', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS3(:,2), fillerrbarS3(:,2)-fillerrbarS3(:,1), fillerrbarS3(:,3)-fillpercerrbarS3(:,2), '--y', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS4(:,2), fillerrbarS4(:,2)-fillerrbarS4(:,1), fillerrbarS4(:,3)-fillpercerrbarS4(:,2), '--m', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS5(:,2), fillerrbarS5(:,2)-fillerrbarS5(:,1), fillerrbarS5(:,3)-fillpercerrbarS5(:,2), '--r', 'LineWidth', 2);
% errorbar([25,50,100], fillerrbarS6(:,2), fillerrbarS6(:,2)-fillerrbarS6(:,1), fillerrbarS6(:,3)-fillpercerrbarS6(:,2), '--g', 'LineWidth', 2);
% title('Percentage of repositories filled up according to storage depletion ratio');
% xlabel('Repo storage filled (%)');
% ylabel('Quantity of repos filled (%)');
% axis([0 125 0 100])
% legend('2 MB/ 3 s', '2 MB/2 s', '2 MB/s', '1 MB/3 s', '1 MB/2 s', '1 MB/s');
% hold off

% figure
% hold on
% bar(reposfill);
% title('Repositories storage used according to storage depletion ratio');
% xlabel('Repo number');
% ylabel('Repo storage used (MB)');
% legend('2 MB/ 3 s', '2 MB/2 s', '2 MB/s', '1 MB/3 s', '1 MB/2 s', '1 MB/s');
% hold off

for repo = 1:c3
    upspeeds1(repo, :) = [mean(SB1(:, repo)), mean(UB1(:, repo)), mean(PB1(:, repo))];
    upspeeds2(repo, :) = [mean(SB2(:, repo)), mean(UB2(:, repo)), mean(PB2(:, repo))];
    upspeeds3(repo, :) = [mean(SB3(:, repo)), mean(UB3(:, repo)), mean(PB3(:, repo))];
    upspeeds4(repo, :) = [mean(SB4(:, repo)), mean(UB4(:, repo)), mean(PB4(:, repo))];
    inspeeds1(repo, :) = mean(RI1(:, repo));
    inspeeds2(repo, :) = mean(RI2(:, repo));
    inspeeds3(repo, :) = mean(RI3(:, repo));
    inspeeds4(repo, :) = mean(RI4(:, repo));
end

figure
stem3(S4, ':.');
title('3D Stem plot of Repos Storage Usage')
xlabel('Repo Number') 
ylabel('Time(s)')
zlabel('Space used(B)')

storage_29 = [S1(:, 29), S2(:, 29), S3(:, 29), S4(:, 29)];

figure
plot(storage_29);
xlabel('Time (s)') 
ylabel('Total storage used (messages stored)')
legend('2 threads', '4 threads', '8 threads', '16 threads');

mdeleted = [M1(10000, :)', M2(10000, :)', M3(10000, :)', M4(10000, :)'];
figure
bar(80:634, mdeleted);
title('No. messages deleted from mobile nodes')
xlabel('Node address') 
ylabel('No. messages deleted')
legend('2 threads', '4 threads', '8 threads', '16 threads')

proc_upspeeds_29 = [PB1(:, 29), PB2(:, 29), PB3(:, 29), PB4(:, 29)];
uproc_upspeeds_29 = [UB1(:, 29), UB2(:, 29), UB3(:, 29), UB4(:, 29)];
static_upspeeds_29 = [SB1(:, 29), SB2(:, 29), SB3(:, 29), SB4(:, 29)];
figure
subplot(3,1,1);
plot(uproc_upspeeds_29);
title('Upload speeds for cloud offloading')
xlabel('Time (s)') 
ylabel('Bandwidth used (B)')
legend('2 threads', '4 threads', '8 threads', '16 threads');
subplot(3,1,2);
plot(proc_upspeeds_29);
title('Upload speeds for unprocessed messages')
xlabel('Time (s)') 
ylabel('Bandwidth used (B)')
legend('2 threads', '4 threads', '8 threads', '16 threads');
subplot(3,1,3);
plot(static_upspeeds_29);
title('Upload speeds for unprocessed messages')
xlabel('Time (s)') 
ylabel('Bandwidth used (B)')
legend('2 threads', '4 threads', '8 threads', '16 threads');

figure
upspeeds1_29 = [SB4(:, 29) UB4(:, 29) PB4(:, 29)];
subplot(1,2,1);
bar(upspeeds1_29, 'stacked');
legend('static message upload', 'cloud offloading upload', 'processed message upload');
xlabel('Time (s)')  
ylabel('Bandwidth used (B)')
inspeeds1_29 = RI4(:, 29);
subplot(1,2,2);
bar(inspeeds1_29);
xlabel('Time (s)')  
ylabel('Bandwidth used (B)')
legend('B/s input');

figure
upspeeds1_29 = [mean(SB1(:, 29)), mean(UB1(:, 29)), mean(PB1(:, 29));
                mean(SB2(:, 29)), mean(UB2(:, 29)), mean(PB2(:, 29)); 
                mean(SB3(:, 29)), mean(UB3(:, 29)), mean(PB3(:, 29)); 
                mean(SB4(:, 29)), mean(UB4(:, 29)), mean(PB4(:, 29))];
subplot(1,2,1);
bar([2, 4, 8, 16], upspeeds1_29, 'stacked');
legend('static message upload', 'cloud offloading upload', 'processed message upload');
xlabel('No. of threads per repository') 
ylabel('Bandwidth used (B)')
inspeeds1_29 = [mean(RI1(:, 29)), mean(RI2(:, 29)), mean(RI3(:, 29)), mean(RI4(:, 29))];
subplot(1,2,2);
bar([2, 4, 8, 16], inspeeds1_29);
legend('messages/s input');
xlabel('No. of threads per repository') 
ylabel('Bandwidth used (B)')

tstorages_29 = [RS4(:, 29) RP4(:, 29)];
figure
bar(tstorages_29, 'stacked');
legend('static message storage', 'processed message storage');
xlabel('Time (s)') 
ylabel('Total storage used (B)')

storages_29 = [mean(RS1(:, 29)), mean(RP1(:, 29));
                mean(RS2(:, 29)), mean(RP2(:, 29)); 
                mean(RS3(:, 29)), mean(RP3(:, 29)); 
                mean(RS4(:, 29)), mean(RP4(:, 29))];
figure
bar([2, 4, 8, 16], storages_29, 'stacked');
legend('static message storage', 'processed message storage');
xlabel('No. of threads per repository')
ylabel('Total storage used (B)')

figure
% upspeeds = [upspeeds1;
%             upspeeds2; 
%             upspeeds3;
%             upspeeds4; 
%             upspeeds5;];
subplot(1,2,1);
bar(upspeeds4, 'stacked');
legend('static message upload', 'cloud offloading upload', 'processed message upload');
xlabel('Repository number') 
ylabel('Bandwidth used (B/s)')
subplot(1,2,2);
bar(inspeeds4);
legend('B/s input');
xlabel('Repository number') 
ylabel('Bandwidth used (B/s)')

