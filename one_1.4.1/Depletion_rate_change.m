% Make a function out of this, that takes the reports folder address and
% the number of runs as variables;

clear
M1 = dlmread('reports1/MDMR1', ' ', 0, 2);
S1 = dlmread('reports1/RAMR1', ' ', 0, 2);
PB1 = dlmread('reports1/RPBWR1', ' ', 0, 2);
UB1 = dlmread('reports1/RUPBWR1', ' ', 0, 2);
SB1 = dlmread('reports1/RSBWR1', ' ', 0, 2);
RI1 = dlmread('reports1/RISR1', ' ', 0, 2);
RP1 = dlmread('reports1/RPrMR1', ' ', 0, 2);
RS1 = dlmread('reports1/RSMR1', ' ', 0, 2);

M2 = dlmread('reports1/MDMR2', ' ', 0, 2);
S2 = dlmread('reports1/RAMR2', ' ', 0, 2);
UB2 = dlmread('reports1/RUPBWR2', ' ', 0, 2);
PB2 = dlmread('reports1/RPBWR2', ' ', 0, 2);
SB2 = dlmread('reports1/RSBWR2', ' ', 0, 2);
RI2 = dlmread('reports1/RISR2', ' ', 0, 2);
RP2 = dlmread('reports1/RPrMR2', ' ', 0, 2);
RS2 = dlmread('reports1/RSMR2', ' ', 0, 2);

M3 = dlmread('reports1/MDMR3', ' ', 0, 2);
S3 = dlmread('reports1/RAMR3', ' ', 0, 2);
PB3 = dlmread('reports1/RPBWR3', ' ', 0, 2);
UB3 = dlmread('reports1/RUPBWR3', ' ', 0, 2);
SB3 = dlmread('reports1/RSBWR3', ' ', 0, 2);
RI3 = dlmread('reports1/RISR3', ' ', 0, 2);
RP3 = dlmread('reports1/RPrMR3', ' ', 0, 2);
RS3 = dlmread('reports1/RSMR3', ' ', 0, 2);

M4 = dlmread('reports1/MDMR4', ' ', 0, 2);
S4 = dlmread('reports1/RAMR4', ' ', 0, 2);
PB4 = dlmread('reports1/RPBWR4', ' ', 0, 2);
UB4 = dlmread('reports1/RUPBWR4', ' ', 0, 2);
SB4 = dlmread('reports1/RSBWR4', ' ', 0, 2);
RI4 = dlmread('reports1/RISR4', ' ', 0, 2);
RP4 = dlmread('reports1/RPrMR4', ' ', 0, 2);
RS4 = dlmread('reports1/RSMR4', ' ', 0, 2);

M5 = dlmread('reports1/MDMR5', ' ', 0, 2);
S5 = dlmread('reports1/RAMR5', ' ', 0, 2);
PB5 = dlmread('reports1/RPBWR5', ' ', 0, 2);
UB5 = dlmread('reports1/RUPBWR5', ' ', 0, 2);
SB5 = dlmread('reports1/RSBWR5', ' ', 0, 2);
RI5 = dlmread('reports1/RISR5', ' ', 0, 2);
RP5 = dlmread('reports1/RPrMR5', ' ', 0, 2);
RS5 = dlmread('reports1/RSMR5', ' ', 0, 2);

M6 = dlmread('reports1/MDMR6', ' ', 0, 2);
S6 = dlmread('reports1/RAMR6', ' ', 0, 2);
UB6 = dlmread('reports1/RUPBWR6', ' ', 0, 2);
PB6 = dlmread('reports1/RPBWR6', ' ', 0, 2);
SB6 = dlmread('reports1/RSBWR6', ' ', 0, 2);
RI6 = dlmread('reports1/RISR6', ' ', 0, 2);
RP6 = dlmread('reports1/RPrMR6', ' ', 0, 2);
RS6 = dlmread('reports1/RSMR6', ' ', 0, 2);

M7 = dlmread('reports1/MDMR7', ' ', 0, 2);
S7 = dlmread('reports1/RAMR7', ' ', 0, 2);
PB7 = dlmread('reports1/RPBWR7', ' ', 0, 2);
UB7 = dlmread('reports1/RUPBWR7', ' ', 0, 2);
SB7 = dlmread('reports1/RSBWR7', ' ', 0, 2);
RI7 = dlmread('reports1/RISR7', ' ', 0, 2);
RP7 = dlmread('reports1/RPrMR7', ' ', 0, 2);
RS7 = dlmread('reports1/RSMR7', ' ', 0, 2);

M8 = dlmread('reports1/MDMR8', ' ', 0, 2);
S8 = dlmread('reports1/RAMR8', ' ', 0, 2);
PB8 = dlmread('reports1/RPBWR8', ' ', 0, 2);
UB8 = dlmread('reports1/RUPBWR8', ' ', 0, 2);
SB8 = dlmread('reports1/RSBWR8', ' ', 0, 2);
RI8 = dlmread('reports1/RISR8', ' ', 0, 2);
RP8 = dlmread('reports1/RPrMR8', ' ', 0, 2);
RS8 = dlmread('reports1/RSMR8', ' ', 0, 2);

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

for inc = 8
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
    
    if inc == 6
        S = S6;
    end
    
    if inc == 7
        S = S7;
    end
    
    if inc == 8
        S = S8;
    end
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
    
    if inc == 5
        maxstorS5 = maxstor;
        fillpercS5 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS5(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS5(repo);
        end
    end
    
    if inc == 6
        maxstorS6 = maxstor;
        fillpercS6 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS6(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS6(repo);           
        end
%         fillerrbarS3 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 7
        maxstorS7 = maxstor;
        fillpercS7 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            upspeeds7(repo, :) = [mean(SB7(:, repo)), mean(UB7(:, repo)), mean(PB7(:, repo))];
            reposfillS7(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS4(repo);           
        end
%         fillerrbarS4 = [sum(filled100(:)), sum(filled50(:)), sum(filled25(:))];
    end
    
    if inc == 8
        maxstorS8 = maxstor;
        fillpercS8 = [fillperc25(:); fillperc50(:); fillperc100(:)];
        for repo = 1:c3
            reposfillS8(repo) = mean(S(:, repo));
            reposfill(repo, inc) = reposfillS8(repo);
        end
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

for repo = 1:c3
    upspeeds1(repo, :) = [mean(SB1(:, repo)), mean(UB1(:, repo)), mean(PB1(:, repo))];
    upspeeds2(repo, :) = [mean(SB2(:, repo)), mean(UB2(:, repo)), mean(PB2(:, repo))];
    upspeeds3(repo, :) = [mean(SB3(:, repo)), mean(UB3(:, repo)), mean(PB3(:, repo))];
    upspeeds4(repo, :) = [mean(SB4(:, repo)), mean(UB4(:, repo)), mean(PB4(:, repo))];    
    upspeeds5(repo, :) = [mean(SB5(:, repo)), mean(UB5(:, repo)), mean(PB5(:, repo))];
    upspeeds6(repo, :) = [mean(SB6(:, repo)), mean(UB6(:, repo)), mean(PB6(:, repo))];
    upspeeds7(repo, :) = [mean(SB7(:, repo)), mean(UB7(:, repo)), mean(PB7(:, repo))];    
    upspeeds8(repo, :) = [mean(SB8(:, repo)), mean(UB8(:, repo)), mean(PB8(:, repo))];
    inspeeds1(repo, :) = mean(RI1(:, repo));
    inspeeds2(repo, :) = mean(RI2(:, repo));
    inspeeds3(repo, :) = mean(RI3(:, repo));
    inspeeds4(repo, :) = mean(RI4(:, repo));    
    inspeeds5(repo, :) = mean(RI5(:, repo)); 
    inspeeds6(repo, :) = mean(RI6(:, repo));
    inspeeds7(repo, :) = mean(RI7(:, repo));    
    inspeeds8(repo, :) = mean(RI8(:, repo));
end

figure
stem3(S1, ':.');
title('3D Stem plot of Repos Storage Usage','fontsize',16)
xlabel('Repo Number','fontsize',12) 
ylabel('Time(s)','fontsize',12)
zlabel('Space used(MB)','fontsize',12)

% figure
% stem3(M3, ':.');
% title('3D Stem plot of Mobile Deleted Messages')
% xlabel('Repo Number') 
% ylabel('Time(s)')
% zlabel('Messages')
% figure
% stem3(R3, ':.');
% title('3D Stem plot of Repos Deleted Messages')
% xlabel('Repo Number') 
% ylabel('Time(s)')
% zlabel('Messages')

storage_29 = [S1(:, 29), S2(:, 29), S3(:, 29), S4(:, 29), S5(:, 29), S6(:, 29), S7(:, 29), S8(:, 29)];

figure
plot(storage_29);
xlabel('Time (s)','fontsize',12) 
ylabel('Total storage used (No. of messages)','fontsize',12)
lgd = legend('5 MB/s', '7 MB/s', '9 MB/s', '10 MB/s', '15 MB/s', '20 MB/s', '25 MB/s', '30 MB/s');
lgd.FontSize = 12;

mdeleted = [M1(10000, :)', M2(10000, :)', M3(10000, :)', M4(10000, :)', M5(10000, :)', M6(10000, :)', M7(10000, :)', M8(10000, :)'];
figure
bar(80:634, mdeleted);
title('No. messages deleted from mobile nodes','fontsize',16)
xlabel('Change in total output BW (B)','fontsize',12) 
ylabel('No. messages deleted','fontsize',12)
lgd = legend('5 MB/s', '7 MB/s', '9 MB/s', '10 MB/s', '15 MB/s', '20 MB/s', '25 MB/s', '30 MB/s');
lgd.FontSize = 9;

proc_upspeeds_29 = [PB1(:, 29), PB2(:, 29), PB3(:, 29), PB4(:, 29), PB5(:, 29), PB6(:, 29), PB7(:, 29), PB8(:, 29)];
uproc_upspeeds_29 = [UB1(:, 29), UB2(:, 29), UB3(:, 29), UB4(:, 29), UB5(:, 29), UB6(:, 29), UB7(:, 29), UB8(:, 29)];
static_upspeeds_29 = [SB1(:, 29), SB2(:, 29), SB3(:, 29), SB4(:, 29), SB5(:, 29), SB6(:, 29), SB7(:, 29), SB8(:, 29)];
figure
% subplot(3,1,1);
% plot(uproc_upspeeds_29);
% title('Upload speeds for cloud offloading','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('5 MB/s', '7 MB/s', '9 MB/s', '10 MB/s', '15 MB/s', '20 MB/s', '25 MB/s', '30 MB/s');
% lgd.FontSize = 9;
subplot(2,1,1);
plot(proc_upspeeds_29);
title('Upload speeds for processed messages','fontsize',16)
xlabel('Time (s)','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
lgd = legend('5 MB/s', '7 MB/s', '9 MB/s', '10 MB/s', '15 MB/s', '20 MB/s', '25 MB/s', '30 MB/s');
lgd.FontSize = 9;
subplot(2,1,2);
plot(static_upspeeds_29);
title('Upload speeds for unprocessed messages','fontsize',16)
xlabel('Time (s)','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
lgd = legend('5 MB/s', '7 MB/s', '9 MB/s', '10 MB/s', '15 MB/s', '20 MB/s', '25 MB/s', '30 MB/s');
lgd.FontSize = 9;

% figure
% upspeeds1_29 = [SB4(:, 29) UB4(:, 29) PB4(:, 29)];
% subplot(1,2,1);
% bar(upspeeds1_29, 'stacked');
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% lgd.FontSize = 9;
% inspeeds1_29 = RI4(:, 29);
% subplot(1,2,2);
% bar(inspeeds1_29);
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)

figure
upspeeds1_29 = [mean(SB1(:, 29)), mean(UB1(:, 29)), mean(PB1(:, 29));
                mean(SB2(:, 29)), mean(UB2(:, 29)), mean(PB2(:, 29)); 
                mean(SB3(:, 29)), mean(UB3(:, 29)), mean(PB3(:, 29)); 
                mean(SB4(:, 29)), mean(UB4(:, 29)), mean(PB4(:, 29)); 
                mean(SB5(:, 29)), mean(UB5(:, 29)), mean(PB5(:, 29)); 
                mean(SB6(:, 29)), mean(UB6(:, 29)), mean(PB6(:, 29)); 
                mean(SB7(:, 29)), mean(UB7(:, 29)), mean(PB7(:, 29)); 
                mean(SB8(:, 29)), mean(UB8(:, 29)), mean(PB8(:, 29))];
subplot(1,2,1);
bar([5, 7, 9, 10, 15, 20, 25, 30], upspeeds1_29, 'stacked');
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
lgd.FontSize = 9;
xlabel('Change in total output BW (MB)','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
inspeeds1_29 = [mean(RI1(:, 29)), mean(RI2(:, 29)), mean(RI3(:, 29)), mean(RI4(:, 29)), mean(RI5(:, 29)), mean(RI6(:, 29)), mean(RI7(:, 29)), mean(RI8(:, 29))];
subplot(1,2,2);
bar([5, 7, 9, 10, 15, 20, 25, 30], inspeeds1_29);
xlabel('Change in total output BW (B)','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)

tstorages_29 = [RS1(:, 29) RP1(:, 29)];
figure
bar(tstorages_29, 'stacked');
lgd = legend('non-processing message storage', 'processing message storage');
lgd.FontSize = 12;
xlabel('Total storage used (B)','fontsize',12) 
ylabel('Time (s)','fontsize',12)

storages_29 = [mean(RS1(:, 29)), mean(RP1(:, 29));
                mean(RS2(:, 29)), mean(RP2(:, 29)); 
                mean(RS3(:, 29)), mean(RP3(:, 29)); 
                mean(RS4(:, 29)), mean(RP4(:, 29)); 
                mean(RS5(:, 29)), mean(RP5(:, 29)); 
                mean(RS6(:, 29)), mean(RP6(:, 29)); 
                mean(RS7(:, 29)), mean(RP7(:, 29)); 
                mean(RS8(:, 29)), mean(RP8(:, 29))];
figure
bar([5, 7, 9, 10, 15, 20, 25, 30], storages_29, 'stacked');
lgd = legend('non-processing message storage', 'processing message storage');
lgd.FontSize = 12;
xlabel('Change in total output BW (B)','fontsize',12)
ylabel('Total storage used (B)','fontsize',12)

figure
% upspeeds = [upspeeds1;
%             upspeeds2; 
%             upspeeds3;
%             upspeeds4; 
%             upspeeds5;];
subplot(1,2,1);
bar(upspeeds1, 'stacked');
title('5MB/s Upload','fontsize',16)
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
lgd.FontSize = 9;
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)
% subplot(3,2,2);
% bar(upspeeds2, 'stacked');
% title('4 processing threads')
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% xlabel('Repository number') 
% ylabel('Bandwidth used (B/s)')
% subplot(3,2,3);
% bar(upspeeds3, 'stacked');
% title('8 processing threads')
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% xlabel('Repository number') 
% ylabel('Bandwidth used (B/s)')
subplot(1,2,2);
bar(upspeeds4, 'stacked');
title('30MB/s Upload','fontsize',16)
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
lgd.FontSize = 9;
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)

upspeeds1_store = upspeeds1(:, 1)';
upspeeds1_cloud = upspeeds1(:, 2)';
upspeeds1_proc = upspeeds1(:, 3)';
upspeeds4_store = upspeeds4(:, 1)';
upspeeds4_cloud = upspeeds4(:, 2)';
upspeeds4_proc = upspeeds4(:, 3)';
upspeeds1_total = upspeeds1(:, 1)' + upspeeds1(:, 2)' + upspeeds1(:, 3)';
upspeeds2_total = upspeeds2(:, 1)' + upspeeds2(:, 2)' + upspeeds2(:, 3)';
upspeeds3_total = upspeeds3(:, 1)' + upspeeds3(:, 2)' + upspeeds3(:, 3)';
upspeeds4_total = upspeeds4(:, 1)' + upspeeds4(:, 2)' + upspeeds4(:, 3)';
upspeeds5_total = upspeeds5(:, 1)' + upspeeds5(:, 2)' + upspeeds5(:, 3)';
upspeeds6_total = upspeeds6(:, 1)' + upspeeds6(:, 2)' + upspeeds6(:, 3)';
upspeeds7_total = upspeeds7(:, 1)' + upspeeds7(:, 2)' + upspeeds7(:, 3)';
upspeeds8_total = upspeeds8(:, 1)' + upspeeds8(:, 2)' + upspeeds8(:, 3)';

figure
plot(1:80, upspeeds1_store, 1:80, upspeeds1_cloud, 1:80, upspeeds1_proc, 1:80, upspeeds4_store, 1:80, upspeeds4_cloud, 1:80, upspeeds4_proc);
title('Processing threads','fontsize',16)
lgd =legend('non-processing message upload for 2', 'cloud offloading upload for 2', 'processed message upload for 2', 'non-processing message upload for 16', 'cloud offloading upload for 16', 'processed message upload for 16');
lgd.FontSize = 9;
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)

figure
yyaxis left
bar(upspeeds1, 'stacked');
title('Upload rates','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)

yyaxis right
plot(1:80, upspeeds1_total, '-+', 1:80, upspeeds2_total, '-o', 1:80, upspeeds3_total, '-*', 1:80, upspeeds4_total, '-x', 1:80, upspeeds5_total, ':o', 1:80, upspeeds6_total, '-*', 1:80, upspeeds7_total, '-x', 1:80, upspeeds8_total, ':o');
lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'message upload for 5MB/s', 'message upload for 8MB/s', 'message upload for 10MB/s', 'message upload for 20MB/s', 'message upload for 30MB/s');
lgd1.FontSize = 9;
ylabel('Bandwidth used (B/s)','fontsize',12)

