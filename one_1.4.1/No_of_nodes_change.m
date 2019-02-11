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
% R6 = dlmread('proc_sims/RDMR6', ' ', 0, 1);
% M6 = dlmread('proc_sims/MDMR6', ' ', 0, 1);
% S6 = dlmread('proc_sims/RSMLR6', ' ', 0, 2);
[r21, c21] = size(M1);
[r22, c22] = size(M2);
[r31, c31] = size(S1);
[r32, c32] = size(S2);
maxstorM1 = zeros(c21, 0);
maxstor = zeros(c31, 0);

% for i = 1:r11
%     maxstorR1(i) = max(R1(i, :));
% end
% maxval1 = max(maxstorR1);

for i = 1:r21
    maxstorM1(i) = max(M1(i, :));
end
maxval2 = max(maxstorM1);

for inc = 3
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
        c3 = c31;
        r3 = r31;
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


% figure
% bar(fillperc100);
% title('Percentage of repositories filled up to 100%');
% xlabel('Time (s)');
% ylabel('Repositories which used about 100% storage (%)');
% 
% figure
% bar(fillperc50);
% title('Percentage of repositories filled up to 50%');
% xlabel('Time (s)');
% ylabel('Repositories which used more than 50% storage (%)');
% 
% figure
% bar(fillperc25);
% title('Percentage of repositories filled up to 25%');
% xlabel('Time (s)');
% ylabel('Repositories which used more than 25% storage (%)');

%figure
%bar3(M);

%figure
%bar3(R);

%figure
%bar3(S, 0.5);

% deleted_29 = [max(R1(:, 29)), max(R2(:, 29)), max(R3(:, 29)), max(R4(:, 29))];
% 
% figure
% plot([1 2 3 4], deleted_29);

mdeleted1 = [mean(M1(10800, 1:40)), mean(M1(10800, 41:240)), mean(M1(10800, 241:255));
             mean(M2(10800, 1:40)), mean(M2(10800, 41:540)), mean(M2(10800, 541:555));
             mean(M3(10800, 1:40)), mean(M3(10800, 41:1040)), mean(M3(10800, 1041:1055))];
% mdeleted2 = M2(10000, :);
% mdeleted3 = M3(10000, :);
% addr1 = 80:334;
% addr2 = 80:634;
% addr3 = 80:1134;

figure
% subplot(1,3,1);
bar([200, 500, 1000], mdeleted1);
lgd = legend('Pedestrian generated', 'Car generated', 'Bus generated');
lgd.FontSize = 12;
ylabel('Average no. of messages deleted per node','fontsize',12);
xlabel('No. of cars in environment','fontsize',12);
% subplot(1,3,2);
% bar(addr2, mdeleted2);
% title('500 cars in environment');
% xlabel('Mobile node address');
% subplot(1,3,3);
% bar(addr3, mdeleted3);
% title('1000 cars in environment');
% xlabel('Mobile node address');

for repo = 1:c3
    upspeeds1(repo, :) = [mean(SB1(:, repo)), mean(UB1(:, repo)), mean(PB1(:, repo))];
    upspeeds2(repo, :) = [mean(SB2(:, repo)), mean(UB2(:, repo)), mean(PB2(:, repo))];
    upspeeds3(repo, :) = [mean(SB3(:, repo)), mean(UB3(:, repo)), mean(PB3(:, repo))];
    inspeeds1(repo, :) = mean(RI1(:, repo));
    inspeeds2(repo, :) = mean(RI2(:, repo));
    inspeeds3(repo, :) = mean(RI3(:, repo));
end

storage_30 = [S1(:, 30), S2(:, 30), S3(:, 30)];

figure
plot(storage_30);
xlabel('Time (s)','fontsize',12) 
ylabel('Total storage used (messages stored)','fontsize',12)
lgd = legend('200 cars', '500 cars', '1000 cars');
lgd.FontSize = 9;

% storage = zeros(3, c31);
% repos = 1:80;
% for repo = 1:80
% storage(:, repo) = [mean(S1(:, repo)); mean(S2(:, repo)); mean(S3(:, repo))];
% end
% 
% figure
% errorbar(repos,storage(2, :), storage(2, :)-storage(1, :), storage(3, :)-storage(2, :));
% xlabel('Repo No.','fontsize',12) 
% ylabel('Total storage used (messages stored)','fontsize',12)
% lgd = legend('200 cars', '500 cars', '1000 cars');
% lgd.FontSize = 9;


% mdeleted = M3(10800, :)';
% figure
% bar(80:1134, mdeleted);
% title('No. messages deleted from mobile nodes','fontsize',16)
% xlabel('Node address','fontsize',12) 
% ylabel('No. messages deleted','fontsize',12)

proc_upspeeds_30 = [PB1(:, 30), PB2(:, 30), PB3(:, 30)];
uproc_upspeeds_30 = [UB1(:, 30), UB2(:, 30), UB3(:, 30)];
static_upspeeds_30 = [SB1(:, 30), SB2(:, 30), SB3(:, 30)];
figure
% subplot(3,1,1);
% plot(uproc_upspeeds_30);
% title('Upload speeds for cloud offloading','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('200 cars', '500 cars', '1000 cars');
% lgd.FontSize = 9;
subplot(2,1,1);
plot(proc_upspeeds_30);
title('Upload speeds for processed messages','fontsize',16)
xlabel('Time (s)','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
lgd = legend('200 cars', '500 cars', '1000 cars');
lgd.FontSize = 9;
subplot(2,1,2);
plot(static_upspeeds_30);
title('Upload speeds for unprocessed messages','fontsize',16)
xlabel('Time (s)','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
lgd = legend('200 cars', '500 cars', '1000 cars');
lgd.FontSize = 9;

% figure
% upspeeds1_30 = [SB3(:, 30) UB3(:, 30) PB3(:, 30)];
% subplot(1,2,1);
% bar(upspeeds1_30, 'stacked');
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% lgd.FontSize = 9;
% xlabel('Time (s)','fontsize',12)  
% ylabel('Bandwidth used (B)','fontsize',12)
% inspeeds1_30 = RI3(:, 30);
% subplot(1,2,2);
% bar(inspeeds1_30);
% xlabel('Time (s)','fontsize',12)  
% ylabel('Bandwidth of input (B)','fontsize',12)

figure
upspeeds1_30 = [mean(SB1(:, 30)), mean(UB1(:, 30)), mean(PB1(:, 30));
                mean(SB2(:, 30)), mean(UB2(:, 30)), mean(PB2(:, 30)); 
                mean(SB3(:, 30)), mean(UB3(:, 30)), mean(PB3(:, 30))];
subplot(1,2,1);
bar([200, 500, 1000], upspeeds1_30, 'stacked');
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
lgd.FontSize = 10;
xlabel('No. of cars in simulation','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
inspeeds1_30 = [mean(RI1(:, 30)), mean(RI2(:, 30)), mean(RI3(:, 30))];
subplot(1,2,2);
bar([200, 500, 1000], inspeeds1_30);
xlabel('No. of cars in simulation','fontsize',12) 
ylabel('Bandwidth of input (B)','fontsize',12)

storages_30 = [mean(RS1(:, 30)), mean(RP1(:, 30));
                mean(RS2(:, 30)), mean(RP2(:, 30)); 
                mean(RS3(:, 30)), mean(RP3(:, 30))];
figure
bar([200, 500, 1000], storages_30, 'stacked');
lgd = legend('non-processing message storage', 'processing message storage');
lgd.FontSize = 9;
xlabel('No. of cars in simulation','fontsize',12)
ylabel('Total storage used (B)','fontsize',12)

upspeeds1_store = upspeeds1(:, 1)';
upspeeds1_cloud = upspeeds1(:, 2)';
upspeeds1_proc = upspeeds1(:, 3)';
upspeeds3_store = upspeeds3(:, 1)';
upspeeds3_cloud = upspeeds3(:, 2)';
upspeeds3_proc = upspeeds3(:, 3)';
upspeeds1_total = upspeeds1(:, 1)' + upspeeds1(:, 2)' + upspeeds1(:, 3)';
upspeeds2_total = upspeeds2(:, 1)' + upspeeds2(:, 2)' + upspeeds2(:, 3)';
upspeeds3_total = upspeeds3(:, 1)' + upspeeds3(:, 2)' + upspeeds3(:, 3)';

figure
plot(1:80, upspeeds1_store, 1:80, upspeeds1_cloud, 1:80, upspeeds1_proc, 1:80, upspeeds3_store, 1:80, upspeeds3_cloud, 1:80, upspeeds3_proc);
title('Processing threads','fontsize',16)
lgd =legend('non-processing message upload for 200', 'cloud offloading upload for 200', 'processed message upload for 200', 'non-processing message upload for 1000', 'cloud offloading upload for 1000', 'processed message upload for 1000');
lgd.FontSize = 9;
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)


figure

subplot(1,2,1);
yyaxis left
bar(upspeeds1, 'stacked');
title('(a) Number of cars vs. up-link BW','fontsize',14)
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)
ylim([0 5*10^6]);
xlim([17 57]);

yyaxis right
plot(1:80, upspeeds1_total, 'r-+', 1:80, upspeeds2_total, 'm-o', 1:80, upspeeds3_total, '-*');
lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'message upload for 200', 'message upload for 500', 'message upload for 1000');
lgd1.FontSize = 9;
ylabel('Bandwidth used (B/s)','fontsize',12)
xlim([17 57]);
ylim([0 5*10^6]);

subplot(1,2,2);
inspeeds = [inspeeds1, inspeeds2, inspeeds3];
bar_handle = bar(inspeeds);
set(bar_handle(1),'FaceColor',[1,0,0])
set(bar_handle(2),'FaceColor',[0,1,0])
set(bar_handle(3),'FaceColor',[0,0,1])
title('(b) Input Bandwidths','fontsize',14)
lgd = legend('200 cars', '500 cars', '1000 cars');
lgd.FontSize = 9;
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)
xlim([17 57]);

repos = 1:80;
figure
hBarGrp=bar(abs(randn(320,2)),'grouped','stacked');  % 160 bars, group by 2
off=hBarGrp(2).XOffset - 0.03;             % hidden property, offset from nominal x
hBar1=bar(repos-off+0.03,upspeeds1,0.25,'stacked'); % draw first stacked as wanted
lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'message upload for 200', 'message upload for 500', 'message upload for 1000');
lgd1.FontSize = 9;

hold all                            % hold axes, don't reset color order position
hBar2=bar(repos+off-0.03,[nan(size(inspeeds1)) inspeeds1],0.30, 'r'); % second with place holder
xlim([17 57]);

hold all                            % hold axes, don't reset color order position
hBar3=bar(repos+2*off-0.03,[nan(size(inspeeds2)) inspeeds2],0.30, 'g'); % second with place holder
xlim([17 57]);

hold all                            % hold axes, don't reset color order position
hBar4=bar(repos+3*off-0.03,[nan(size(inspeeds3)) inspeeds3],0.30, 'b'); % second with place holder
xlim([17 57]);


